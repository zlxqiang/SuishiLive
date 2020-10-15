package com.suishi.live.app.widgets;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lifengwei on 2017/7/5.
 * 循环的数据结构
 */
public class CycleStruct<E> {

    private List<E> list = new ArrayList<>();
    private int index;
    private int startIndex;
    private boolean hasGetStarted = false;
    public void start(int index){
        index = index%list.size();
        this.index = index;
        this.startIndex = index;
        hasGetStarted = false;
    }

    public void addData(E e){
        list.add(e);
    }

    public E get(){
        hasGetStarted = true;
        E e;
        if(index<list.size()){
            e = list.get(index);
        }else{
            e = list.get(index-list.size());
        }
        index++;
        return e;
    }

    public boolean canNext(){
        if(list.size()==0){
            return false;
        }
        return !hasGetStarted||
                (index!=startIndex&&
                        (index-list.size())!=startIndex);//没有取过数据或是数据指针没有到达开始时的下标
    }
}