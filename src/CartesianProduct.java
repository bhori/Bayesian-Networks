import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CartesianProduct {

//    public static Set<Set<Object>> cartesianProduct(Set<?> sets) {
//        if (sets.length < 2)
//            throw new IllegalArgumentException(
//                    "Can't have a product of fewer than two sets (got " +
//                            sets.length + ")");
//
//        return _cartesianProduct(0, sets);
//    }
//
//    private static Set<Set<Object>> _cartesianProduct(int index, Set<?> sets) {
//        Set<Set<Object>> ret = new HashSet<Set<Object>>();
//        if (index == sets.length) {
//            ret.add(new HashSet<Object>());
//        } else {
//            for (Object obj : sets[index]) {
//                for (Set<Object> set : _cartesianProduct(index+1, sets)) {
//                    set.add(obj);
//                    ret.add(set);
//                }
//            }
//        }
//        return ret;
//    }

    public static <T> List<List<T>> cartesianProduct(List<T>... lists) {

        List<List<T>> product = new ArrayList<List<T>>();

        for (List<T> list : lists) {

            List<List<T>> newProduct = new ArrayList<List<T>>();

            for (T listElement : list) {

                if (product.isEmpty()) {

                    List<T> newProductList = new ArrayList<T>();
                    newProductList.add(listElement);
                    newProduct.add(newProductList);
                } else {

                    for (List<T> productList : product) {

                        List<T> newProductList = new ArrayList<T>(productList);
                        newProductList.add(listElement);
                        newProduct.add(newProductList);
                    }
                }
            }

            product = newProduct;
        }

        return product;
    }

    public static void main(String[] args) {
//        Set<String> s1 = new HashSet<String>();
//        Set<String> s2 = new HashSet<String>();
//        s1.add("true");
//        s1.add("false");
//        s2.add("true");
//        s2.add("false");
//        Set<Set<String>> s3 = new HashSet<Set<String>>();
//        s3.add(s1);
//        s3.add(s2);
//        Set<Set<Object>> s4 = cartesianProduct(s3);
//        System.out.println(s4);

        List<String> l1 = new ArrayList<String>();
        List<String> l2 = new ArrayList<String>();
        l1.add("t");
        l1.add("f");
        l2.add("t");
        l2.add("f");
        l2.add("e");
//        List<String> l3 = new ArrayList<String>();
//        l3.add("t");
//        l3.add("f");
        List<List<String>> l5 = new ArrayList<List<String>>();
        l5.add(l1);
        l5.add(l2);
//        l5.add(l3);
//        List<List<String>> l4 = cartesianProduct(l1, l2);
//        System.out.println(l4);
//        List<List<String>> l6 = cartesianProduct(l5.toArray());

        List<List<String>> comb = cartesianProduct(l5.toArray(new ArrayList[]{new ArrayList<String>()}));
        for (List<String> l7:comb) {
            System.out.println(l7);

        }

    }
}