package br.com.codenation.service;

import java.util.*;
import java.util.stream.Collectors;

import br.com.codenation.model.OrderItem;
import br.com.codenation.model.Product;
import br.com.codenation.repository.ProductRepository;
import br.com.codenation.repository.ProductRepositoryImpl;

public class OrderServiceImpl implements OrderService {

	private ProductRepository productRepository = new ProductRepositoryImpl();

	/**
	 * Calculate the sum of all OrderItems
	 */

		@Override
		public Double calculateOrderValue(List<OrderItem> items){
			Double valorTotal =	items.stream().map(order -> {
				Product produto = productRepository.findById(order.getProductId()).get();
				Double valor = order.getQuantity() * produto.getValue();
				if ( produto.getIsSale()){
					valor = valor * 0.80;
				}
				return valor;
			}).reduce(0.0, Double::sum);


			return valorTotal;
		}



		/**
		 *
		 * Map from idProduct List to Product Set
		 */
		@Override
		public Set<Product> findProductsById(List<Long> ids) {
			return ids.parallelStream()
					.map(id -> productRepository.findById(id).orElse(null))
					.filter(Objects::nonNull)
					.collect(Collectors.toSet());
		}

		/**
		 * Calculate the sum of all Orders(List<OrderIten>)
		 */
		@Override
		public Double calculateMultipleOrders(List<List<OrderItem>> orders) {
			List<Double> valorOrdens = orders.stream().map(this::calculateOrderValue).collect(Collectors.toList());
			return valorOrdens.stream().reduce(0.0, Double::sum);

		}
			/**
		 * Group products using isSale attribute as the map key
		 */
		@Override
		public Map<Boolean, List<Product>> groupProductsBySale(List<Long> productIds) {

			List<Product> produtos = productIds.stream().map(id-> productRepository.findById(id).get()).collect(Collectors.toList());
			Map<Boolean,List<Product>> group = new HashMap<>();
			List<Product> isSaleList = produtos.stream().filter(Product::getIsSale).collect(Collectors.toList());
			List<Product> isNotSaleList = produtos.stream().filter(produto->!produto.getIsSale()).collect(Collectors.toList());
			group.put(true,  isSaleList );
			group.put(false,  isNotSaleList );
			return group;
		}
	}