#include "ThreadQueue.h"

/**
 */

template<class T>
ThreadQueue<T>::ThreadQueue() {

}

template<class T>
ThreadQueue<T>::ThreadQueue(ThreadQueue const
                                      &other) {
    std::lock_guard<std::mutex> lk(other.mut);
    data_queue = other.data_queue;
}

template<class T>
void ThreadQueue<T>::push(T new_value)//入队操作
{
    std::lock_guard<std::mutex> lk(mut);
    data_queue.
            push(new_value);
    data_cond.

            notify_one();

}

template<class T>
void ThreadQueue<T>::wait_and_pop(T &value)//直到有元素可以删除为止
{
    std::unique_lock<std::mutex> lk(mut);
    data_cond.wait(lk, [this] { return !data_queue.empty(); });
    value = data_queue.front();
    data_queue.pop();
}

template<class T>
std::shared_ptr<T> ThreadQueue<T>::wait_and_pop() {
    std::unique_lock<std::mutex> lk(mut);
    data_cond.wait(lk, [this] { return !data_queue.empty(); });
    std::shared_ptr<T> res(std::make_shared<T>(data_queue.front()));
    data_queue.pop();
    return res;
}

template<class T>
bool ThreadQueue<T>::try_pop(T &value)//不管有没有队首元素直接返回
{
    std::lock_guard<std::mutex> lk(mut);
    if (data_queue.empty())
        return false;
    value = data_queue.front();
    data_queue.pop();
    return true;
}

template<class T>
std::shared_ptr<T> ThreadQueue<T>::try_pop() {
    std::lock_guard<std::mutex> lk(mut);
    if (data_queue.empty())
        return NULL;
    std::shared_ptr<T> res(std::make_shared<T>(data_queue.front()));
    data_queue.pop();
    return res;
}

template<class T>
bool ThreadQueue<T>::empty() const {
    return data_queue.empty();
}

template<class T>
void ThreadQueue<T>::clear() {
    std::lock_guard<std::mutex> lk(mut);
    std::queue<T> empty;
    std::swap(data_queue, empty);
}

