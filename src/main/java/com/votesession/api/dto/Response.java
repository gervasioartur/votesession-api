package com.votesession.api.dto;

public record Response(int code, String status, Object body) {}