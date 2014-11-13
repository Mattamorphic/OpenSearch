/*
 * Copyright Elasticsearch B.V. and/or licensed to Elasticsearch B.V. under one
 * or more contributor license agreements. Licensed under the Elastic License;
 * you may not use this file except in compliance with the Elastic License.
 */
package org.elasticsearch.alerts.client;

import org.elasticsearch.action.*;
import org.elasticsearch.action.support.TransportAction;
import org.elasticsearch.alerts.transport.actions.delete.*;
import org.elasticsearch.alerts.transport.actions.get.*;
import org.elasticsearch.alerts.transport.actions.index.*;
import org.elasticsearch.alerts.transport.actions.stats.*;
import org.elasticsearch.client.support.Headers;
import org.elasticsearch.common.collect.ImmutableMap;
import org.elasticsearch.common.inject.Inject;
import org.elasticsearch.threadpool.ThreadPool;

public class AlertsClient implements AlertsClientInterface {

    private final Headers headers;
    private final ThreadPool threadPool;
    private final ImmutableMap<GenericAction, TransportAction> internalActions;

    @Inject
    public AlertsClient(ThreadPool threadPool, Headers headers, TransportIndexAlertAction transportIndexAlertAction,
                        TransportGetAlertAction transportGetAlertAction, TransportDeleteAlertAction transportDeleteAlertAction,
                        TransportAlertStatsAction transportAlertStatsAction) {
        this.headers = headers;
        this.threadPool = threadPool;
        internalActions = ImmutableMap.<GenericAction, TransportAction>builder()
                .put(IndexAlertAction.INSTANCE, transportIndexAlertAction)
                .put(GetAlertAction.INSTANCE, transportGetAlertAction)
                .put(DeleteAlertAction.INSTANCE, transportDeleteAlertAction)
                .put(AlertsStatsAction.INSTANCE, transportAlertStatsAction)
                .build();
    }

    @Override
    public GetAlertRequestBuilder prepareGetAlert(String alertName) {
        return new GetAlertRequestBuilder(this, alertName);
    }

    @Override
    public GetAlertRequestBuilder prepareGetAlert() {
        return new GetAlertRequestBuilder(this);
    }

    public void getAlert(GetAlertRequest request, ActionListener<GetAlertResponse> response){
        execute(GetAlertAction.INSTANCE, request, response);
    }

    @Override
    public ActionFuture<GetAlertResponse> getAlert(GetAlertRequest request) {
        return execute(GetAlertAction.INSTANCE, request);
    }

    @Override
    public DeleteAlertRequestBuilder prepareDeleteAlert(String alertName) {
        return new DeleteAlertRequestBuilder(this, alertName);
    }

    @Override
    public DeleteAlertRequestBuilder prepareDeleteAlert() {
        return new DeleteAlertRequestBuilder(this);
    }

    @Override
    public void deleteAlert(DeleteAlertRequest request, ActionListener<DeleteAlertResponse> response) {
        execute(DeleteAlertAction.INSTANCE, request, response);
    }

    @Override
    public ActionFuture<DeleteAlertResponse> deleteAlert(DeleteAlertRequest request) {
        return execute(DeleteAlertAction.INSTANCE, request);
    }

    @Override
    public IndexAlertRequestBuilder prepareIndexAlert(String alertName) {
        return new IndexAlertRequestBuilder(this, alertName);
    }

    @Override
    public IndexAlertRequestBuilder prepareIndexAlert() {
        return new IndexAlertRequestBuilder(this, null);
    }

    @Override
    public void indexAlert(IndexAlertRequest request, ActionListener<IndexAlertResponse> response) {
        execute(IndexAlertAction.INSTANCE, request, response);
    }

    @Override
    public ActionFuture<IndexAlertResponse> indexAlert(IndexAlertRequest request) {
        return execute(IndexAlertAction.INSTANCE, request);
    }

    @Override
    public ActionFuture<AlertsStatsResponse> alertsStats(AlertsStatsRequest request) {
        return execute(AlertsStatsAction.INSTANCE, request);
    }

    @Override
    public AlertsStatsRequestBuilder prepareAlertsStats() {
        return new AlertsStatsRequestBuilder(this);
    }

    @Override
    public void alertsStats(AlertsStatsRequest request, ActionListener<AlertsStatsResponse> listener) {
        execute(AlertsStatsAction.INSTANCE, request, listener);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <Request extends ActionRequest, Response extends ActionResponse, RequestBuilder extends ActionRequestBuilder<Request, Response, RequestBuilder, AlertsClientInterface>> ActionFuture<Response> execute(Action<Request, Response, RequestBuilder, AlertsClientInterface> action, Request request) {
        headers.applyTo(request);
        TransportAction<Request, Response> transportAction = internalActions.get((AlertsClientAction)action);
        return transportAction.execute(request);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <Request extends ActionRequest, Response extends ActionResponse, RequestBuilder extends ActionRequestBuilder<Request, Response, RequestBuilder, AlertsClientInterface>> void execute(Action<Request, Response, RequestBuilder, AlertsClientInterface> action, Request request, ActionListener<Response> listener) {
        headers.applyTo(request);
        TransportAction<Request, Response> transportAction = internalActions.get((AlertsClientAction)action);
        transportAction.execute(request, listener);
    }

    @Override
    public <Request extends ActionRequest, Response extends ActionResponse, RequestBuilder extends ActionRequestBuilder<Request, Response, RequestBuilder, AlertsClientInterface>> RequestBuilder prepareExecute(Action<Request, Response, RequestBuilder, AlertsClientInterface> action) {
        return action.newRequestBuilder(this);
    }

    @Override
    public ThreadPool threadPool() {
        return threadPool;
    }
}
