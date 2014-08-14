package multithreading.concurrency.rw.customlock;


class NotReentrantReadWriteLock {

	private int readers = 0;

	private int writers = 0;

	private int writeRequests = 0;

	public synchronized void lockRead() throws InterruptedException {
		while (writers > 0 || writeRequests > 0) {
			wait();
		}
		readers++;
	}

	public synchronized void unlockRead() {
		readers--;
		notifyAll();
	}

	public synchronized void lockWrite() throws InterruptedException {
		writeRequests++;

		while (readers > 0 || writers > 0) {
			wait();
		}
		writeRequests--;
		writers++;
	}

	public synchronized void unlockWrite() throws InterruptedException {
		writers--;
		notifyAll();
	}
}

class Buffer<T> {

	private NotReentrantReadWriteLock readWriteLock = new NotReentrantReadWriteLock();

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
		new Thread(new Writter(2000, sharedBuffer)).start();
		new Thread(new Writter(5000, sharedBuffer)).start();
	}
}

