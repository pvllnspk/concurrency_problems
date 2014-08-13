package multithreading.concurrency.cp.locks;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

class BlockingQueue<T> {

	private final List<T> queue = new LinkedList<T>();

	private final ReentrantLock lock = new ReentrantLock();

	private Condition emptyQueue = lock.newCondition();

	private Condition fullQueue = lock.newCondition();

	private int capacity = 10;

	public BlockingQueue(int capacity) {
		this.capacity = capacity;
	}

	public void put(T object) throws InterruptedException {
		try {
			lock.lock();

			while (queue.size() == capacity) {
				fullQueue.await();
			}
			if (queue.size() == 0) {
				emptyQueue.signalAll();
			}
			queue.add(object);

			System.out.println("Consumer #" + Thread.currentThread().getId() + " put  " + queue.size());

		} finally {

			lock.unlock();
		}
	}

	public T take() throws InterruptedException {
		try {
			lock.lock();

			while (queue.size() == 0) {
				emptyQueue.await();
			}
			if (queue.size() == capacity) {
				fullQueue.signalAll();
			}
			System.out.println("Producer #" + Thread.currentThread().getId() + " take " + (queue.size() - 1));

			return queue.remove(0);

		} finally {

			lock.unlock();
		}
	}
}

class Producer implements Runnable {

	private final BlockingQueue<Object> queue;

	private int priority;

	public Producer(int priority, BlockingQueue<Object> queue) {
		this.priority = priority;
		this.queue = queue;
	}

	@Override
	public void run() {
		while (true) {
			try {
				Thread.sleep(priority);
				queue.put("object");
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}

class Consumer implements Runnable {

	private final BlockingQueue<Object> queue;

	private int priority;

	public Consumer(int priority, BlockingQueue<Object> queue) {
		this.priority = priority;
		this.queue = queue;
	}

	@Override
	public void run() {
		while (true) {
			try {
				Thread.sleep(priority);
				queue.take();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}

public class ConsumerProducer {

	private static final int SHARED_BUFFER_CAPACITY = 10;

	public static void main(String[] args) {

		BlockingQueue<Object> sharedBuffer = new BlockingQueue<Object>(SHARED_BUFFER_CAPACITY);

		new Thread(new Producer(200, sharedBuffer)).start();
		new Thread(new Producer(300, sharedBuffer)).start();
		new Thread(new Consumer(1000, sharedBuffer)).start();
		new Thread(new Consumer(1000, sharedBuffer)).start();
	}
}
