#!/usr/bin/env python3
"""Compile and run the local HTTP/provider tests without starting Minecraft."""
import argparse
import os
from pathlib import Path
import subprocess


def main():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument('--java-home', type=Path, required=True)
    parser.add_argument('--paper-home', type=Path, required=True)
    parser.add_argument('--annotations', type=Path, required=True)
    args = parser.parse_args()
    project = Path(__file__).resolve().parent.parent
    jdk = args.java_home.resolve()
    server = args.paper_home.resolve()
    classes = project / 'build/skin-test-classes'
    classes.mkdir(parents=True, exist_ok=True)
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
        str(jdk / 'bin/javac'), '-proc:none', '--release', '25',
        '-classpath', cp, '-d', str(classes),
        str(project / 'tests/java/SkinFetchTest.java'),
    ], env=env, check=True)
    subprocess.run([
        str(jdk / 'bin/java'), '-classpath', str(classes) + os.pathsep + cp,
        'io.github.gonalez.znpcs.skin.SkinFetchTest',
    ], env=env, check=True, timeout=40)


if __name__ == '__main__':
    main()
