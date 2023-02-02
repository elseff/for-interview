package ru.elseff.demo.persistense;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(schema = "public", name = "prices")
public class Price {

    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @OneToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "product_id",
            referencedColumnName = "id",
            nullable = false, unique = true)
    private Product product;

    @Column(name = "value", nullable = false)
    private Long value;

    public Price(Long value) {
        this.value = value;
    }
}
