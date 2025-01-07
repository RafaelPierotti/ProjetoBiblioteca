package br.com.projeto.biblioteca.service;

import java.util.List;

public interface IConvertData {
    <T> List<T> getList(String json, Class<T> tClass);

    <T> T getData (String json, Class<T> tClass);

}
