package fr.AxelVatan.CMWLink.Common.WebServer.Injector;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandler;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.concurrent.Callable;

import com.google.common.collect.Lists;

class BootstrapList implements List<Object> {

	private List<Object> delegate;
	private ChannelHandler handler;

	public BootstrapList(List<Object> delegate, ChannelHandler handler) {
		this.delegate = delegate;
		this.handler = handler;
		for (Object item : this) {
			processElement(item);
		}
	}

	@Override
	public synchronized boolean add(Object element) {
		processElement(element);
		return delegate.add(element);
	}

	@Override
	public synchronized boolean addAll(Collection<? extends Object> collection) {
		List<Object> copy = Lists.newArrayList(collection);
		for (Object element : copy) {
			processElement(element);
		}
		return delegate.addAll(copy);
	}

	@Override
	public synchronized Object set(int index, Object element) {
		Object old = delegate.set(index, element);
		if (old != element) {
			unprocessElement(old);
			processElement(element);
		}
		return old;
	}

	protected void processElement(Object element) {
		if (element instanceof ChannelFuture) {
			processBootstrap((ChannelFuture) element);
		}
	}

	protected void unprocessElement(Object element) {
		if (element instanceof ChannelFuture) {
			unprocessBootstrap((ChannelFuture) element);
		}
	}

	protected void processBootstrap(ChannelFuture future) {
		future.channel().pipeline().addFirst(handler);
	}

	protected void unprocessBootstrap(ChannelFuture future) {
		final Channel channel = future.channel();
		channel.eventLoop().submit(new Callable<Object>() {
			@Override
			public Object call() throws Exception {
				channel.pipeline().remove(handler);
				return null;
			}
		});
	}

	public synchronized void close() {
		for (Object element : this)
			unprocessElement(element);
	}

	public synchronized int size() {
		return delegate.size();
	}

	public synchronized boolean isEmpty() {
		return delegate.isEmpty();
	}

	public boolean contains(Object o) {
		return delegate.contains(o);
	}

	public synchronized Iterator<Object> iterator() {
		return delegate.iterator();
	}

	public synchronized Object[] toArray() {
		return delegate.toArray();
	}

	public synchronized <T> T[] toArray(T[] a) {
		return delegate.toArray(a);
	}

	public synchronized boolean remove(Object o) {
		return delegate.remove(o);
	}

	public synchronized boolean containsAll(Collection<?> c) {
		return delegate.containsAll(c);
	}

	public synchronized boolean addAll(int index, Collection<? extends Object> c) {
		return delegate.addAll(index, c);
	}

	public synchronized boolean removeAll(Collection<?> c) {
		return delegate.removeAll(c);
	}

	public synchronized boolean retainAll(Collection<?> c) {
		return delegate.retainAll(c);
	}

	public synchronized void clear() {
		delegate.clear();
	}

	public synchronized Object get(int index) {
		return delegate.get(index);
	}

	public synchronized void add(int index, Object element) {
		delegate.add(index, element);
	}

	public synchronized Object remove(int index) {
		return delegate.remove(index);
	}

	public synchronized int indexOf(Object o) {
		return delegate.indexOf(o);
	}

	public synchronized int lastIndexOf(Object o) {
		return delegate.lastIndexOf(o);
	}

	public synchronized ListIterator<Object> listIterator() {
		return delegate.listIterator();
	}

	public synchronized ListIterator<Object> listIterator(int index) {
		return delegate.listIterator(index);
	}

	public synchronized List<Object> subList(int fromIndex, int toIndex) {
		return delegate.subList(fromIndex, toIndex);
	}
}