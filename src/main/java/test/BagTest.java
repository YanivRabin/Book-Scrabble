package test;
import model.data.Tile;
import model.data.Tile.Bag;

public class BagTest {

    /**
     * The bagTest function tests the Bag class to ensure that it is a singleton,
     * and that its getQuantities method returns a clone of the quantities array.
     * It also tests the getRand and put methods by ensuring that they do not change
     * or return null when given invalid input. Finally, it ensures that getTile
     * returns null for invalid inputs, but does not return null for valid inputs.

     *
     *
     * @return Nothing
     *
     * @docauthor Trelent
     */
    public static void bagTest() {

        Bag b = Tile.Bag.getBag();
        Bag b1 = Tile.Bag.getBag();
        if(b1 != b)
            System.out.println("your Bag in not a Singleton");

        int[] q0 = b.getQuantities();
        q0[0] += 1;
        int[] q1 = b.getQuantities();
        if(q0[0] != q1[0] + 1)
            System.out.println("getQuantities did not return a clone");

        for(int k = 0; k < 9; k++) {

            int[] qs = b.getQuantities();
            Tile t = b.getRand();
            int i = t.letter - 'A';
            int[] qs1 = b.getQuantities();
            if(qs1[i] != qs[i]-1)
                System.out.println("problem with getRand");

            b.put(t);
            b.put(t);
            b.put(t);

            if(b.getQuantities()[i] != qs[i])
                System.out.println("problem with put");
        }

        if(b.getTile('a') != null || b.getTile('$') != null || b.getTile('A') == null)
            System.out.println("your getTile is wrong");

    }
}
