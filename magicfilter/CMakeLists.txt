# For more information about using CMake with Android Studio, read the
# documentation: https://d.android.com/studio/projects/add-native-code.html

# Sets the minimum version of CMake required to build the native library.

cmake_minimum_required(VERSION 3.4.1)


#set(CMAKE_LIBRARY_OUTPUT_DIRECTORY ${CMAKE_SOURCE_DIR}/src/main/jniLibs/${ANDROID_ABI})
# Creates and names a library, sets it as either STATIC
# or SHARED, and provides the relative paths to its source code.
# You can define multiple libraries, and CMake builds them for you.
# Gradle automatically packages shared libraries with your APK.



add_library( # Sets the name of the library.
             filter

             # Sets the library as a shared library.
             SHARED

             # Provides a relative path to your source file(s).
             src/main/cpp/beautify/MagicBeautify.cpp
             src/main/cpp/bitmap/BitmapOperation.cpp
             src/main/cpp/bitmap/Conversion.cpp
             src/main/cpp/MagicJni.cpp
            )

#引用的头文件
include_directories(
                    src/main/cpp/beautify/MagicBeautify.h
                    src/main/cpp/bitmap/BitmapOperation.h
                    src/main/cpp/bitmap/Conversion.h
                    src/main/cpp/bitmap/JniBitmap.h
                    )

# Searches for a specified prebuilt library and stores the path as a
# variable. Because CMake includes system libraries in the search path by
# default, you only need to specify the name of the public NDK library
# you want to add. CMake verifies that the library exists before
# completing its build.

find_library( # Sets the name of the path variable.
              log-lib

              # Specifies the name of the NDK library that
              # you want CMake to locate.
              log )
find_library(bitmap-lib
             jnigraphics)

find_library(GLESv2-lib GLESv2)

# Specifies libraries CMake should link to your target library. You
# can link multiple libraries, such as libraries you define in this
# build script, prebuilt third-party libraries, or system libraries.
# 链接库
target_link_libraries( # Specifies the target library.
                       filter
                       ${log-lib}
                       ${bitmap-lib}
                       ${GLESv2-lib}
                      )