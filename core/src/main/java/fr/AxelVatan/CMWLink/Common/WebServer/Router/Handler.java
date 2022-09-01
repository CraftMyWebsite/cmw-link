package fr.AxelVatan.CMWLink.Common.WebServer.Router;

public interface Handler<R, E> {

	R handle(E event);

}
