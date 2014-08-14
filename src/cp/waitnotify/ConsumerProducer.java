package cp.waitnotify;

import java.util.LinkedList;
import java.util.List;

class BlockingQueue<T> {

	private final List<T> queue = new LinkedList<T>();

	private int capacity = 10;

	public BlockingQueue(int capacity) {
		this.capacity = capacity;
	}

	public synchronized void put(T object) throws InterruptedException {
		while (queue.size() == capacity) {
			wait();
		}
		if (queue.size() == 0) {
			notifyAll();
		}
		queue.add(object);

		System.out.println("Consumer #" + Thread.currentThread().getId() + " put  " + queue.size());
	}

	public synchronized T take() throws InterruptedException {
		while (queue.size() == 0) {
			wait();
		}
		if (queue.size() == capacity) {
			notifyAll();
		}
		System.out.println("Producer #" + Thread.currentThread().getId() + " take " + (queue.size() - 1));

		return queue.remove(0);
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
