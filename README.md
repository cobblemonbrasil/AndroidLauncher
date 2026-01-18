<div align="center">  
    <img src="https://github.com/cobblemonbrasil/AndroidLauncher/blob/main/app_pojavlauncher/src/main/assets/pojavlauncher.png?raw=true" align="center" width="254" pinnacle="254" alt="Cobblemon_Brasil logo">    
    <h1>AndroidLauncher</h1>    
</div>  

[![Discord](https://img.shields.io/discord/1115735772763521106?style=for-the-badge&logo=discord&label=%20&color=%23424549)](https://discord.cobblemonbrasil.com.br)
[![Play Store](https://img.shields.io/endpoint?style=for-the-badge&color=green&logo=google-play&logoColor=green&url=https%3A%2F%2Fplay.cuzi.workers.dev%2Fplay%3Fi%3Dbr.com.cobblemonbrasil.androidlauncher%26gl%3Dbr%26hl%3Dpt-BR%26l%3DStore%26m%3D%24rating%2520%25E2%25AD%2590%2520%2524version)](https://play.google.com/store/apps/details?id=br.com.cobblemonbrasil.androidlauncher)

<br>

An upstream fork of [MojoLauncher](https://github.com/MojoLauncher/MojoLauncher/) modified to run the [Cobblemon Realms](https://cobblemonrealms.com.br) server modpack.

## Information
- We thank the developers of the original project. This fork was created to facilitate player entry by automatically downloading and updating our modpack, as well as pre-configuring and bringing new default controls focused on Cobblemon to the launcher.
- We commit to converting any feature or fix not directly related to the modification's functionality (e.g., the rebrand) into a Pull Request in the original project.

## Changes
- refactor: rebrand to Cobblemon Realms, remove ely.by authentication method, make Microsoft login use own Cobblemon Realms data, removed no used languages, removed instance customize ([7547a7d](https://github.com/cobblemonbrasil/AndroidLauncher/commit/7547a7d165a045542ceeeedc15220f4115c74855))
- feat(modification): auto-update and fixed instance to our modpack ([3f00ce9](https://github.com/cobblemonbrasil/AndroidLauncher/commit/3f00ce9d6aa88110d4025143f242009b8dfc5fea))
- misc: updated controls, license information added to the app, removed no used jre's and more.. ([2121d31](https://github.com/cobblemonbrasil/AndroidLauncher/commit/2121d31a9e7ec9cd08f3671df7f4a198c8697641), [e070598](https://github.com/cobblemonbrasil/AndroidLauncher/commit/e070598ee5079f8e03f5c98214de306c9993ad3e), [14400d8](https://github.com/cobblemonbrasil/AndroidLauncher/commit/14400d8235a80f4a6011d753c8cc8b30bf0c2f65), [3f790e3](https://github.com/cobblemonbrasil/AndroidLauncher/commit/3f790e3f2b0262e204c9099be9a4cacccb8d49cf))

## License
* MojoLauncher is licensed under [GNU LGPLv3](https://github.com/MojoLauncher/MojoLauncher/blob/v3_openjdk/LICENSE).
* Subject to the terms of the license, all modified MojoLauncher code is distributed here.

## Credits & Third party components and their licenses (if available)
- [PojavLauncher](https://github.com/PojavLauncherTeam/PojavLauncher): [GNU LGPLv3 License](https://github.com/PojavLauncherTeam/PojavLauncher/blob/v3_openjdk/LICENSE)
- [Boardwalk](https://github.com/zhuowei/Boardwalk) (JVM Launcher): Unknown License/[Apache License 2.0](https://github.com/zhuowei/Boardwalk/blob/master/LICENSE) or GNU GPLv2.
- Android Support Libraries: [Apache License 2.0](https://android.googlesource.com/platform/prebuilts/maven_repo/android/+/master/NOTICE.txt).
- [GL4ES](https://github.com/PojavLauncherTeam/gl4es): [MIT License](https://github.com/ptitSeb/gl4es/blob/master/LICENSE).<br>
- [OpenJDK](https://github.com/PojavLauncherTeam/openjdk-multiarch-jdk8u): [GNU GPLv2 License](https://openjdk.java.net/legal/gplv2+ce.html).<br>
- [LWJGL3](https://github.com/MojoLauncher/lwjgl3): [BSD-3 License](https://github.com/LWJGL/lwjgl3/blob/master/LICENSE.md).
- [Mesa 3D Graphics Library](https://gitlab.freedesktop.org/mesa/mesa): [MIT License](https://docs.mesa3d.org/license.html).
- [pro-grade](https://github.com/pro-grade/pro-grade) (Java sandboxing security manager): [Apache License 2.0](https://github.com/pro-grade/pro-grade/blob/master/LICENSE.txt).
- [bhook](https://github.com/bytedance/bhook) (Used for exit code trapping): [MIT license](https://github.com/bytedance/bhook/blob/main/LICENSE).
- [Authlib-Injector](https://github.com/yushijinhun/authlib-injector) (Used for authorisation via ely.by): [AGPL-3.0](https://github.com/yushijinhun/authlib-injector/blob/develop/LICENSE).
- [alsoft](https://github.com/kcat/openal-soft/) (Audio output library): [GNU LIBRARY GENERAL PUBLIC LICENSE](https://github.com/kcat/openal-soft/blob/master/COPYING) and [modified PFFFT](https://github.com/kcat/openal-soft/blob/master/LICENSE-pffft).
- [oboe](https://github.com/google/oboe): [Apache License 2.0](https://github.com/google/oboe/blob/main/LICENSE).
- Thanks to [Mineskin](https://mineskin.eu/) for providing Minecraft avatars.