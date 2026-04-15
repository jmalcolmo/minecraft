# CLAUDE.md — testMod

## Repo
https://github.com/jmalcolmo/minecraft

## Mod
- **ID:** name.modid
- **Name:** testMod
- **Purpose:** Adds a new food item
- **Loader:** Fabric
- **Minecraft:** 26.1.2
- **Java:** JDK 21

## Rules
- Short answers. No filler.
- MVP first. No feature creep.
- Get it working, then stop.

## Git Workflow
- `main` = production
- `release` = stable releases
- `dev` = active development
- Branch off `dev` for features: `feature/your-feature`
- Open a PR before large changes
- Commit often, keep messages short and descriptive
- Stage and push practical chunks — no massive single commits

## File Structure
```
MCMODTEST/
    .github/workflows/
        build.yml
    gradle/wrapper/
        gradle-wrapper.jar
        gradle-wrapper.properties
    src/
        client/
            java/name/modid/client/
                mixin/
                    ExampleClientMixin.java
                TestModClient.java
                TestModDataGenerator.java
            resources/
                testmod.client.mixins.json
        main/
            java/name/modid/
                mixin/
                    ExampleMixin.java
                TestMod.java
            resources/
                assets/testmod/
                    icon.png
                fabric.mod.json
                testmod.mixins.json
    .gitattributes
    .gitignore
    build.gradle
    gradle.properties
    gradlew
    gradlew.bat
    LICENSE
    README.md
    settings.gradle
```

## Priorities
1. Register food item
2. Add texture
3. Add recipe
4. Done