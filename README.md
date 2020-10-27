# AndroidWidget
Android自定义控件。

# Layout

| Name                | Description                                              |
| ------------------- | -------------------------------------------------------- |
| PullToRefreshLayout | 一个简单的下拉刷新，上拉加载库，通过NestedScroll实现。   |
| BottomSheetLayout   | 一个简单的BottomSheet布局，通过setTranslationY方法实现。 |

# 使用

### Gradle

~~~groovy
dependencies {
    implementation 'com.cere:widget:1.0.1'
}
~~~

### XML

##### PullToRefreshLayout

~~~xml
<com.cere.widget.PullToRefreshLayout
        ...
        app:body="@id/body"
        app:header="@id/header"
        app:footer="@id/footer">
    
    <LinearLayout
            android:id="@+id/header"
            android:layout_width="match_parent"
            android:layout_height="match_parent"
            android:background="@color/teal_200"
            android:gravity="center"
            android:orientation="horizontal">

            <ImageView
                android:id="@+id/header_image"
                android:layout_width="30dp"
                android:layout_height="30dp"
                android:layout_marginEnd="10dp"
                android:padding="5dp"
                android:scaleType="centerInside"
                android:src="@drawable/ic_drop_down" />

            <TextView
                android:id="@+id/header_text"
                android:layout_width="wrap_content"
                android:layout_height="wrap_content"
                android:layout_gravity="center"
                android:layout_marginStart="10dp"
                android:gravity="center"
                android:text="下拉刷新" />

        </LinearLayout>

        <androidx.recyclerview.widget.RecyclerView
            android:id="@+id/body"
            android:layout_width="match_parent"
            android:overScrollMode="never"
            android:layout_height="match_parent" />

        <LinearLayout
            android:id="@+id/footer"
            android:layout_gravity="bottom"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:orientation="horizontal" />
    
</com.cere.widget.PullToRefreshLayout>
~~~

##### BottomSheetLayout

~~~xml
<com.cere.widget.BottomSheetLayout
    android:layout_width="match_parent"
    android:layout_height="match_parent">
    
    ...
    
    <LinearLayout
        ...
        android:layout_gravity="bottom">
        ...
    </LinearLayout>
    
</com.cere.widget.BottomSheetLayout>
~~~



# License

~~~
Copyright 2020 CheRevir

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
~~~



