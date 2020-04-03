# album-cover-equalizer-view
Simple equalizer visualization view for Android OS
[![](https://jitpack.io/v/naz013/album-cover-equalizer-view.svg)](https://jitpack.io/#naz013/album-cover-equalizer-view)

Design by Samuel C.: [Dribble](https://dribbble.com/shots/10746051-009-Music-Player)

Screenshot

<img src="https://github.com/naz013/album-cover-equalizer-view/raw/master/res/screen.png" width="400" alt="Screenshot">

Sample APP
--------
[Google Play](https://play.google.com/store/apps/details?id=com.github.naz013.albumcoverequalizer.example)

Download
--------
Download latest version with Gradle:
```groovy
repositories {
    maven { url 'https://jitpack.io' }
}

dependencies {
     implementation 'com.github.naz013:album-cover-equalizer-view:1.0'
}
```

Usage
-----
Default:
```xml
 <com.github.naz013.albumcoverequalizer.AlbumCoverEqView
        android:id="@+id/albumView"
        android:layout_width="256dp"
        android:layout_height="256dp"
        android:layout_gravity="center_horizontal"
        android:layout_marginTop="32dp"
        app:acv_animationSpeed="slow"
        app:acv_barColor="?colorPrimary"
        app:acv_dividerColor="?colorPrimary"
        app:acv_dividerWidth="2dp"
        app:acv_numberOfBars="15" />
```

-----
And then use:
```kotlin
val array = newFloatArray(albumView.getNumberOfBars())
albumView.setWaveHeights(array)
```

License
-------

    Copyright 2020 Nazar Sukhovych

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.