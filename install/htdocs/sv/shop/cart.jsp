<%@ page language="java"
import="java.util.*, java.text.*, imcode.util.shop.*"
%>
<html>
  <head>
    <title>Shopping cart</title>
  </head>

  <body>

  <form action="@servleturl@/PutInShoppingCart" method="POST">
    <input type="hidden" name="next" value="/shop/cart.jsp" />
    <input type="hidden" name="priceformatgroupingseparator" value="." />

    <%
      /* Get a swedish DecimalFormat. */
      DecimalFormat priceFormat = (DecimalFormat)NumberFormat.getInstance(new Locale("sv","SE")) ;
      priceFormat.setDecimalSeparatorAlwaysShown(true) ;

      /* The default swedish grouping separator is a space. We want a dot. */
      DecimalFormatSymbols priceFormatSymbols = new DecimalFormatSymbols() ;
      priceFormatSymbols.setGroupingSeparator('.') ;
      priceFormat.setDecimalFormatSymbols(priceFormatSymbols) ;

      ShoppingCart cart = (ShoppingCart)session.getAttribute(ShoppingCart.SESSION_NAME) ;
      if (null == cart) {
        cart = new ShoppingCart() ;
      }
      ShoppingItem[] items = cart.getItems() ;
      double totalPrice = 0 ;
    %>

    <table border="1">
      <tr>
        <th>Description 1</th>
        <th>Description 2</th>
        <th>Description 3</th>
        <th>Description 4</th>
        <th>Description 5</th>
        <th>Quantity</th>
        <th>Price</th>
        <th>Remove</th>
      </tr>
      <%
         for (int i = 0; i < items.length; ++i) {
	   ShoppingItem item = items[i] ;
	   int itemCount = cart.countItem(item) ;
	   double itemPrice = item.getPrice() ;
	   Map desc = item.getDescriptions() ;
	   totalPrice += itemPrice * itemCount ;
      %>
      <tr>
        <td><%= desc.get(new Integer(1)) %></td>
        <td><%= desc.get(new Integer(2)) %></td>
        <td><%= desc.get(new Integer(3)) %></td>
        <td><%= desc.get(new Integer(4)) %></td>
        <td><%= desc.get(new Integer(5)) %></td>
        <td><input type="text" name="number_<%= i %>" value="<%= itemCount %>" /></td>
        <td><%= priceFormat.format(itemPrice) %></td>
        <td><input type="checkbox" value="1" name="remove_<%= i %>"></td>
      </tr>
      <input type="hidden" value="<%= desc.get(new Integer(1)) %>" name="desc1_<%= i %>" />
      <input type="hidden" value="<%= desc.get(new Integer(2)) %>" name="desc2_<%= i %>" />
      <input type="hidden" value="<%= desc.get(new Integer(3)) %>" name="desc3_<%= i %>" />
      <input type="hidden" value="<%= desc.get(new Integer(4)) %>" name="desc4_<%= i %>" />
      <input type="hidden" value="<%= desc.get(new Integer(5)) %>" name="desc5_<%= i %>" />
      <input type="hidden" value="<%= priceFormat.format(itemPrice) %>" name="price_<%= i %>" />
      <% } %>
    </table>
    <p>
      Total items: <%= cart.countItems() %>
    </p>
    <p>
      Total price: <%= priceFormat.format(totalPrice) %>
    </p>
    <input type="submit" name="submit" value="Modify shopping cart">
    </form>
  </body>
</html>
