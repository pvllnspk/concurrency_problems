package multithreading.concurrency.rw.customlock;


class CustomReadWriteLock {

	public void lockRead() {

	}

	public void unlockRead() {

	}

	public void lockWrite() {

	}

	public void unlockWrite() {

	}
}

class Buffer<T> {

	private CustomReadWriteLock readWriteLock = new CustomReadWriteLock();

	public void write(T object) throws InterruptedException {
		try {
			readWriteLock.lockWrite();

			System.out.println("Writer #" + Thread.currentThread().getId() + " started writing.");

			Thread.sleep(3000);

			System.out.println("Writer #" + Thread.currentThread().getId() + " finished writing.");

		} finally {

			readWriteLock.unlockWrite();
		}
	}

	public void read() throws InterruptedException {
		try {
			readWriteLock.lockRead();

			System.out.println("Reader #" + Thread.currentThread().getId() + " started reading.");

			Thread.sleep(1000);

			System.out.println("Reader #" + Thread.currentThread().getId() + " finished reading.");

		} finally {

			readWriteLock.unlockRead();
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
