/**
 */
#ifndef JIANXIFFMPEG_THREADSAFE_QUEUE
#define JIANXIFFMPEG_THREADSAFE_QUEUE

#include <queue>
#include <memory>
#include <mutex>
#include <condition_variable>

template<typename T>
class ThreadQueue {

private:

    mutable std::mutex mut;

    std::queue<T> data_queue;

    std::condition_variable data_cond;
public:
    ThreadQueue();

    ThreadQueue(ThreadQueue const &other);

    /**
     * 插入数据
     * @param new_value
     */
    void push(T new_value);

    void wait_and_pop(T &value);

    std::shared_ptr<T> wait_and_pop();

    bool try_pop(T &value);

    /**
     * 弹出数据
     * @return
     */
    std::shared_ptr<T> try_pop();

    /**
     * 判断是否等于空
     * @return
     */
    bool empty() const;

    /**
     * 清空队列
     */
    void clear();
};

#endif //JIANXIFFMPEG_THREADSAFE_QUEUE_CPP
