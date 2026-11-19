#!/usr/bin/env bash
# Wraps a jpackage app image into a single AppImage file.
# Usage: packaging/appimage.sh <app image dir> <name> <icon.png> <category> <runtime> <output.AppImage>
set -euo pipefail
image="$1" name="$2" icon="$3" category="$4" runtime="$5" output="$6"

appdir="$(mktemp -d)/$name.AppDir"
mkdir -p "$appdir"
cp -r "$image"/. "$appdir/"
cp "$icon" "$appdir/$name.png"
ln -s "bin/$name" "$appdir/AppRun"
cat > "$appdir/$name.desktop" <<DESKTOP
[Desktop Entry]
Type=Application
Name=$name
Exec=$name
Icon=$name
Categories=$category;
DESKTOP

squashfs="$appdir.squashfs"
mksquashfs "$appdir" "$squashfs" -root-owned -noappend -comp gzip -quiet
cat "$runtime" "$squashfs" > "$output"
chmod +x "$output"
rm -rf "$(dirname "$appdir")"
echo "Built $output"
