package com.minimarket.service;

import com.minimarket.entity.OrdenCompra;

import java.util.List;

public interface OrdenCompraService {
    List<OrdenCompra> findAll();
    OrdenCompra findById(Long id);
    OrdenCompra recibir(Long id);
}
