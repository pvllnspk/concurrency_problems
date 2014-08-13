package multithreading.concurrency.rw.locks;

import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

class Buffer<T> {

	private ReadWriteLock readWriteLock = new ReentrantReadWriteLock(true);

	public void write(T object) throws InterruptedException {
		try {
			readWriteLock.writeLock().lock();

			System.out.println("Writer #" + Thread.currentThread().getId() + " started writing.");

			Thread.sleep(3000);

			System.out.println("Writer #" + Thread.currentThread().getId() + " finished writing.");

		} finally {

			readWriteLock.writeLock().unlock();
		}
	}

	public void read() throws InterruptedException {
		try {
			readWriteLock.readLock().lock();

			System.out.println("Reader #" + Thread.currentThread().getId() + " started reading.");

			Thread.sleep(1000);

			System.out.println("Reader #" + Thread.currentThread().getId() + " finished reading.");

		} finally {

			readWriteLock.readLock().unlock();
		}
	}
}

class Reader implements Runnable {

	private final Buffer<Object> buffer;

	private int priority;

	public Reader(int priority, Buffer<Object> buffer) {
		this.priority = priority;
		this.buffer = buffer;
	}

	@Override
	public void run() {
		while (true) {
			try {
				Thread.sleep(priority);
				buffer.read();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}

class Writter implements Runnable {

	private final Buffer<Object> buffer;

	private int priority;

	public Writter(int priority, Buffer<Object> buffer) {
		this.priority = priority;
		this.buffer = buffer;
	}

	@Override
	public void run() {
		while (true) {
			try {
				Thread.sleep(priority);
				buffer.write("Object");
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}

public class ReadersWriters {

	public static void main(String[] args) {

		Buffer<Object> sharedBuffer = new Buffer<Object>();

		new Thread(new Reader(1, sharedBuffer)).start();
		new Thread(new Reader(700, sharedBuffer)).start();
		new Thread(new Reader(100, sharedBuffer)).start();
		new Thread(new Writter(200, sharedBuffer)).start();
		new Thread(new Writter(400, sharedBuffer)).start();
	}
}
