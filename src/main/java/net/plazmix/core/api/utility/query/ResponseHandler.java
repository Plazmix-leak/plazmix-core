package net.plazmix.core.api.utility.query;

public interface ResponseHandler<R, O> {

    R handleResponse(O o);
}
