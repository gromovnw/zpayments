package it.gromov.zpayments.storage.entity;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@DatabaseTable(tableName = "cart_entries")
public class CartEntry {

    @DatabaseField(generatedId = true)
    private int id;

    @DatabaseField(canBeNull = false, index = true, width = 32)
    private String nickname;

    @DatabaseField(canBeNull = false, width = 64, uniqueCombo = true)
    private String orderId;

    @DatabaseField(canBeNull = false, width = 190, uniqueCombo = true)
    private String taskKey;

    @DatabaseField(canBeNull = false, width = 700)
    private String command;

    @DatabaseField(width = 190)
    private String itemTitle;

    @DatabaseField
    private int itemCount;

    @DatabaseField
    private double itemPrice;

    @DatabaseField
    private double orderTotal;

    @DatabaseField
    private long createdAt;
}
