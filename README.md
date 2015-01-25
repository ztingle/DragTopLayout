DragTopLayout
---
Sometimes we need to show a top view above the ViewPager or ListView, just like Google Calendar or Google Contact. DragTopLayout is a ViewGroup that contains a content view and a top menu view. You can show the top menu view just drag down the content view at the right time. Or drag it up to fold.

Here is a show case:
pic

Usage
---
Add the dependency to your build.gradle:

```
    dependencies {
	    compile 'com.github.chenupt.android:dragtoplayout:1.0.0@aar'
	}
```
Add the DragTopLayout in your layout file.

```xml
		<github.chenupt.dragtoplayout.DragTopLayout
            android:layout_width="match_parent"
            android:layout_height="match_parent">

            <!--menu view-->
            <LinearLayout
                android:layout_width="match_parent"
                android:layout_height="wrap_content"
                android:gravity="center"
                android:orientation="vertical">
                ...
            </LinearLayout>

            <!--content view-->
            <LinearLayout
                android:orientation="vertical"
                android:layout_width="match_parent"
                android:layout_height="match_parent">
	           ...
            </LinearLayout>
            
        </github.chenupt.dragtoplayout.DragTopLayout>
```
Init the DragTopLayout in your activity code.
```java
DragTopLayout.from(this)
	.open()
	.setRefreshRadio(1.4f)
	.listener(new DragTopLayout.SimplePanelListener() {
		...
	}).setup(dragLayout);
```
Developed By
---
 * Chenupt - <chenupt@outlook.com>

License
---

    Copyright 2015 chenupt

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.

