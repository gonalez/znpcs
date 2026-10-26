#!/usr/bin/env python3
"""Overlay the Paper 26.3 sources onto the pinned, published ZNPCs 5.3 binary.

Usage: python3 build.py --java-home /path/to/jdk25 --base-jar znpcs-5.3.jar
    --paper-home /path/to/prepared-paper-26.3 --placeholder-api /path/to/placeholderapi.jar
    --annotations /path/to/annotations-26.0.2.jar
The Paper folder must contain versions/26.3/paper-26.3.jar and libraries/**/*.jar.
Prepare it by running Paper once with -Dpaperclip.patchonly=true.
"""
import argparse
import hashlib
import os
from pathlib import Path
import shutil
import subprocess
import zipfile

BASE_SHA256 = 'e26b8d912fee7177e2ec5342c28157cf588b96a4c5052f9402b0755fee9426b1'
PAPER_SHA256 = '6552d037cb5ead1634498cf28d5ac2d63ffee9125867b47db5de8b2e48c4756d'

def build():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument('--java-home', type=Path, required=True)
    parser.add_argument('--base-jar', type=Path, required=True)
    parser.add_argument('--paper-home', type=Path, required=True)
    parser.add_argument('--placeholder-api', type=Path, required=True)
    parser.add_argument('--annotations', type=Path, required=True)
    args = parser.parse_args()
    project = Path(__file__).resolve().parent
    args.java_home = args.java_home.resolve()
    args.base_jar = args.base_jar.resolve()
    args.paper_home = args.paper_home.resolve()
    args.placeholder_api = args.placeholder_api.resolve()
    args.annotations = args.annotations.resolve()
    paper = args.paper_home / 'versions/26.3/paper-26.3.jar'
    for file, expected in [(args.base_jar, BASE_SHA256), (paper, PAPER_SHA256)]:
        if hashlib.sha256(file.read_bytes()).hexdigest() != expected:
            raise SystemExit(f'Wrong input version: {file}')
    classes = project / 'build/classes'
    if classes.exists(): shutil.rmtree(classes)
    classes.mkdir(parents=True)
    libraries = sorted((args.paper_home / 'libraries').rglob('*.jar'))
    classpath = os.pathsep.join(str(p) for p in [args.base_jar, paper, *libraries, args.placeholder_api, args.annotations])
    env = dict(os.environ)
    if os.name != 'nt':
        env['LD_LIBRARY_PATH'] = os.pathsep.join([str(args.java_home / 'lib'), str(args.java_home / 'lib/server'), env.get('LD_LIBRARY_PATH', '')])
    sources = sorted((project / 'src/main/java').rglob('*.java'))
    result = subprocess.run([str(args.java_home / 'bin/javac'), '-proc:none', '--release', '25',
        '-encoding', 'UTF-8', '-classpath', classpath, '-d', str(classes), *map(str, sources)], env=env)
    if result.returncode:
        raise SystemExit(result.returncode)
    output = project / 'build/znpcs-5.4-SNAPSHOT-paper-26.3.jar'
    resources = project / 'src/main/resources'
    replacements = {str(p.relative_to(classes)).replace(os.sep, '/'): p.read_bytes() for p in classes.rglob('*.class')}
    replacements.update({str(p.relative_to(resources)).replace(os.sep, '/'): p.read_bytes() for p in resources.rglob('*') if p.is_file()})
    replacements['META-INF/MANIFEST.MF'] = b'Manifest-Version: 1.0\r\npaperweight-mappings-namespace: mojang\r\n\r\n'
    def write_entry(target, name, data):
        info = zipfile.ZipInfo(name, date_time=(2026, 10, 26, 0, 0, 0))
        info.compress_type = zipfile.ZIP_DEFLATED
        info.external_attr = 0o100644 << 16
        target.writestr(info, data)
    with zipfile.ZipFile(args.base_jar) as base, zipfile.ZipFile(output, 'w', zipfile.ZIP_DEFLATED) as target:
        for info in base.infolist():
            if info.filename in replacements or info.is_dir(): continue
            # Signatures from the original archive could not validate the modified archive.
            if info.filename.upper().startswith('META-INF/') and info.filename.upper().endswith(('.SF', '.RSA', '.DSA')): continue
            write_entry(target, info.filename, base.read(info))
        for name, data in sorted(replacements.items()): write_entry(target, name, data)
    print(output)
    print('SHA256:', hashlib.sha256(output.read_bytes()).hexdigest())

if __name__ == '__main__': build()
