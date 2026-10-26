#!/usr/bin/env python3
"""Build a separate integration-test plugin; never install or run it automatically."""
import argparse
import os
from pathlib import Path
import shutil
import subprocess
import zipfile


def main():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument('kind', choices=['smoke', 'live', 'failure'])
    parser.add_argument('--java-home', type=Path, required=True)
    parser.add_argument('--paper-home', type=Path, required=True)
    parser.add_argument('--annotations', type=Path, required=True)
    args = parser.parse_args()
    project = Path(__file__).resolve().parent.parent
    jdk, server = args.java_home.resolve(), args.paper_home.resolve()
    name = {'smoke': 'PortSmokeTest', 'live': 'PortLiveTest', 'failure': 'PortFailureTest'}[args.kind]
    classes = project / 'build' / (args.kind + '-plugin-classes')
    if classes.exists():
        shutil.rmtree(classes)
    classes.mkdir(parents=True)
    cp = os.pathsep.join(map(str, [
        project / 'build/znpcs-5.4-SNAPSHOT-paper-26.3.jar',
        server / 'versions/26.3/paper-26.3.jar',
        *sorted((server / 'libraries').rglob('*.jar')),
        args.annotations.resolve(),
    ]))
    env = dict(os.environ)
    if os.name != 'nt':
        env['LD_LIBRARY_PATH'] = os.pathsep.join([
            str(jdk / 'lib'), str(jdk / 'lib/server'), env.get('LD_LIBRARY_PATH', '')
        ])
    subprocess.run([
        str(jdk / 'bin/javac'), '-proc:none', '--release', '25', '-classpath', cp,
        '-d', str(classes), str(project / 'tests/java' / (name + '.java')),
    ], env=env, check=True)
    descriptor = f"name: {name}\nversion: 1.0\nmain: {name}\napi-version: '26.3'\n"
    descriptor += ('softdepend' if args.kind == 'failure' else 'depend') + ': [ServersNPC]\n'
    if args.kind == 'live':
        descriptor += 'commands:\n  porttest:\n    description: Local integration-test callback\n'
    target = project / 'build' / (name + '.jar')
    with zipfile.ZipFile(target, 'w', zipfile.ZIP_DEFLATED) as jar:
        for path in sorted(classes.rglob('*.class')):
            jar.write(path, path.relative_to(classes).as_posix())
        jar.writestr('plugin.yml', descriptor)
        jar.writestr('META-INF/MANIFEST.MF', 'Manifest-Version: 1.0\r\npaperweight-mappings-namespace: mojang\r\n\r\n')
    print(target)


if __name__ == '__main__':
    main()
