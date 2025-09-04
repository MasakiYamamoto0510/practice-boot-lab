package com.example.w2.order;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    OrderRepository repo;

    @InjectMocks
    OrderService service;

    @Test
    void place_savesOrder_andReturnsSavedEntity() {
        // arrange
        List<OrderItem> items = List.of(
                new OrderItem("pizza", new BigDecimal("1200"), 1),
                new OrderItem("cola", new BigDecimal("150"), 2)
        );
        // save時にid採番された体で返す
        when(repo.save(any(Order.class))).thenAnswer(inv -> {
            Order o = inv.getArgument(0, Order.class);
            o.setId(1L);
            return o;
        });

        // act
        Order saved = service.place("user-1", items);

        // assert
        assertThat(saved.getId()).isEqualTo(1L);
        assertThat(saved.totalAmount()).isEqualByComparingTo("1500");
        ArgumentCaptor<Order> cap = ArgumentCaptor.forClass(Order.class);
        verify(repo).save(cap.capture());
        assertThat(cap.getValue().getStatus()).isEqualTo(OrderStatus.NEW);
    }

    @Test
    void place_rejectsEmptyItems() {
        assertThatThrownBy(() -> service.place("u", List.of()))
                .isInstanceOf(IllegalArgumentException.class);
        verifyNoInteractions(repo);
    }
    
    @Test
    void totalOf_returnSum_whenFound() {
        Order o = new Order(10L, "u", List.of(
                new OrderItem("a", new BigDecimal("100"), 3),
                new OrderItem("b", new BigDecimal("250"), 1)
        ));
        when(repo.findById(10L)).thenReturn(Optional.of(o));
        
        BigDecimal total = service.totalOf(10L);
        
        assertThat(total).isEqualByComparingTo("550");
        verify(repo).findById(10L);
    }
    
    @Test
    void totalOf_throws_whenNotFound() {
        when(repo.findById(99L)).thenReturn(Optional.empty());
        
        assertThatThrownBy(() -> service.totalOf(99L))
                .isInstanceOf(IllegalArgumentException.class);
    }
    
    @Test
    void cancel_changesStatus_andPersists() {
        Order o = new Order(7L, "u", List.of(new OrderItem("x", new BigDecimal("1"), 1)));
        when(repo.findById(7L)).thenReturn(Optional.of(o));

        service.cancel(7L);

        assertThat(o.getStatus()).isEqualTo(OrderStatus.CANCELED);
        verify(repo).save(o);
    }

    @Test
    void cancel_throws_whenNotFound() {
        when(repo.findById(123L)).thenReturn(Optional.empty());

        assertThatThrownBy( () -> service.cancel(123L))
                .isInstanceOf(IllegalArgumentException.class);
        verify(repo, never()).save(any());
    }
}
