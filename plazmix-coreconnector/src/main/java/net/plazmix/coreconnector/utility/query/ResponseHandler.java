package net.plazmix.coreconnector.utility.query;

public interface ResponseHandler<R, O> {

    R handleResponse(O o);
}
