#!/bin/sh
# Builds the mod jar for every supported Minecraft version branch into dist/,
# using a throwaway git worktree per branch so the current working tree is
# never touched. Local-use only; dist/ and .build-worktrees/ are gitignored.
#
# main and 26.1 target Minecraft 26.x, which requires Java 25 to run Gradle
# itself (not just to compile). Make sure JAVA_HOME points at a JDK 25 (or
# newer) before running this script, or those two branches will fail.
set -e

mkdir -p dist

for branch in main 26.1 1.21 1.21.2 1.21.4 1.21.5 1.21.6 1.21.9 1.21.11 1.20.1; do
    wt=".build-worktrees/$branch"
    git worktree add -f "$wt" "$branch"

    (cd "$wt" && ./gradlew build)

    mcver=$(sed -n 's/^minecraft_version=//p' "$wt/gradle.properties")
    modver=$(sed -n 's/^mod_version=//p' "$wt/gradle.properties")
    cp "$wt/build/libs/dropdummyitems-$modver.jar" \
        "dist/dropdummyitems-$modver-mc$mcver.jar"

    git worktree remove --force "$wt"
done
