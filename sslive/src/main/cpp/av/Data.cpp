//
// Created by admin on 2018/3/22.
//
//
#include "Data.h"

Data::Data() {

}

void Data::freeData() {
    if(mData){
        free(mData);
    }
}

Data::~Data() {

}