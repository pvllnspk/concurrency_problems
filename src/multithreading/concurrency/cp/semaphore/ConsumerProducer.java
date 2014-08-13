package multithreading.concurrency.cp.semaphore;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Semaphore;

class BlockingQueue<T> {

	private final List<T> queue = new LinkedList<T>();

	private final Semaphore mutex = new Semaphore(1);

	private final Semaphore fullQueue = new Semaphore(0);

	private final Semaphore emptyQueue;

	public BlockingQueue(int capacity) {
		emptyQueue = new Semaphore(capacity);
	}

	public void put(T object) throws InterruptedException {
		emptyQueue.acquire();
		mutex.acquire();

		queue.add(object);
		System.out.println("Consumer #" + Thread.currentThread().getId() + " put  " + queue.size());

		mutex.release();
		fullQueue.release();
	}

	public T take() throws InterruptedException {
		fullQueue.acquire();
		mutex.acquire();

		T value = queue.remove(0);
		System.out.println("Producer #" + Thread.currentThread().getId() + " take " + queue.size());

		mutex.release();
		emptyQueue.release();

		return value;
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
