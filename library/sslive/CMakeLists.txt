# For more information about using CMake with Android Studio, read the
# documentation: https://d.android.com/studio/projects/add-native-code.html

# Sets the minimum version of CMake required to build the native library.

cmake_minimum_required(VERSION 3.4.1)
#添加调试信息
#set(CMAKE_CXX_FLAGS_DEBUG "$ENV{CXXFLAGS} -O0 -Wall -g -ggdb")
#set(CMAKE_LIBRARY_OUTPUT_DIRECTORY ${CMAKE_SOURCE_DIR}/src/main/jniLibs/${ANDROID_ABI})
# Creates and names a library, sets it as either STATIC
# or SHARED, and provides the relative paths to its source code.
# You can define multiple libraries, and CMake builds them for you.
# Gradle automatically packages shared libraries with your APK.

#引用的头文件
set(include-yuvheaders ${CMAKE_SOURCE_DIR}/src/main/cpp/yuv)
include_directories(${include-yuvheaders})


set(include-ffmpeg ${CMAKE_SOURCE_DIR}/src/main/cpp/include)
include_directories(${include-ffmpeg})


#添加库 动态库为SHARED  静态库就是STATIC
add_library(avcodec SHARED IMPORTED)
set_target_properties(avcodec
  PROPERTIES IMPORTED_LOCATION
  ${CMAKE_SOURCE_DIR}/src/main/jniLibs/${ANDROID_ABI}/libavcodec-57.so)
#
add_library(ffmpeg SHARED IMPORTED)
set_target_properties(ffmpeg
                      PROPERTIES
                      IMPORTED_LOCATION ${CMAKE_SOURCE_DIR}/src/main/jniLibs/armeabi-v7a/libffmpeg.so)

add_library(libyuv SHARED IMPORTED)
set_target_properties(libyuv
                      PROPERTIES
                      IMPORTED_LOCATION ${CMAKE_SOURCE_DIR}/src/main/jniLibs/${ANDROID_ABI}/libyuv.so)

#自定义库
file(GLOB native_src "src/main/cpp/av/*.cpp")
file(GLOB native_src_1 "src/main/cpp/av/audio/*.cpp")
file(GLOB native_src_2 "src/main/cpp/av/stream/*.cpp")
file(GLOB native_src_3 "src/main/cpp/av/video/*.cpp")

add_library( # Sets the name of the library.
             ffmpeg-handle
             # Sets the library as a shared library.
             SHARED

             # Provides a relative path to your source file(s).
             # Associated headers in the same location as their source
             # file are automatically included.
             ${native_src}
             ${native_src_1}
             ${native_src_2}
             ${native_src_3}
             src/main/cpp/LiveManager.cpp
             src/main/cpp/debug.cpp
        src/main/cpp/FpsTools.cpp
             )

find_library( # Sets the name of the path variable.
              log-lib

              # Specifies the name of the NDK library that
              # you want CMake to locate.
              log )

# 链接库
target_link_libraries( # Specifies the target library.
                       ffmpeg-handle
                       ffmpeg
                       libyuv
                       android
                       ${log-lib} )

