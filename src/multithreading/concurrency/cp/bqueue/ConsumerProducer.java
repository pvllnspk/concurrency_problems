package multithreading.concurrency.cp.bqueue;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

class SharedBuffer<T> extends LinkedBlockingQueue<T> {

	private static final long serialVersionUID = 1L;

	private static final Object putOutputLock = new Object();

	private static final Object takeOutputLock = new Object();

	public SharedBuffer(int capacity) {
		super(capacity);
	}

	@Override
	public void put(T e) throws InterruptedException {
		synchronized (putOutputLock) {
			super.put(e);
			System.out.println("Consumer #" + Thread.currentThread().getId() + " put  " + size());
		}
	}

	@Override
	public T take() throws InterruptedException {
		synchronized (takeOutputLock) {
			System.out.println("Producer #" + Thread.currentThread().getId() + " take " + (size() - 1));
			return super.take();
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

		BlockingQueue<Object> sharedBuffer = new SharedBuffer<Object>(SHARED_BUFFER_CAPACITY);

		new Thread(new Producer(200, sharedBuffer)).start();
		new Thread(new Producer(300, sharedBuffer)).start();
		new Thread(new Consumer(1000, sharedBuffer)).start();
		new Thread(new Consumer(1000, sharedBuffer)).start();
	}
}
