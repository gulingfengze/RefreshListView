# RefreshListView
A refresh ListView that enables pull to refresh  with the animations.
<br>
![1](http://d.hiphotos.baidu.com/image/w%3D310/sign=facd0e055e4e9258a63480efac83d1d1/c2fdfc039245d68827d154c5acc27d1ed31b2484.jpg)
![2](http://g.hiphotos.baidu.com/image/w%3D310/sign=64b60258bbde9c82a665ff8e5c8080d2/d788d43f8794a4c28bf0014a06f41bd5ac6e3984.jpg)
![3](http://a.hiphotos.baidu.com/image/w%3D310/sign=3889f64e9dcad1c8d0bbfa264f3f67c4/962bd40735fae6cd734e4d0007b30f2443a70fdc.jpg)
![4](http://c.hiphotos.baidu.com/image/w%3D310/sign=9f0d0299af86c91708035438f93c70c6/34fae6cd7b899e510574dc754aa7d933c9950ddc.jpg)
# Usage
###Eclipse
You have to download RefreshListView and import the ***refreshlistview_library*** folder in your Eclipse.
Then you need to go to Properties > Android in your project and add RefreshListView as a Library.
###AndroidStudio
Gradle:
<br>
```xml
    dependencies {
    compile project(':refreshlistview_library')
}
```
Layout:
```xml
      <wq.refreshlistview_library.RefreshListView
       android:id="@+id/rlv_test"
       android:layout_width="match_parent"
       android:layout_height="match_parent">
```
