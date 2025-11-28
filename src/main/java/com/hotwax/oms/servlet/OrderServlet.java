package com.hotwax.oms.servlet;

import com.google.gson.Gson;
import com.hotwax.oms.entity.OrderHeader;
import com.hotwax.oms.entity.OrderItem;
import com.hotwax.oms.util.DBConnection;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@WebServlet("/orders/*")
public class OrderServlet extends HttpServlet {

    private Gson gson = new Gson();

    // ------------------- POST: Create Order OR Add Item -------------------
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        PrintWriter out = resp.getWriter();
        String pathInfo = req.getPathInfo();

        try (Connection conn = DBConnection.getConnection()) {
            BufferedReader reader = req.getReader();

            // Scenario 1: Create Order (Path is null or "/")
            if (pathInfo == null || pathInfo.equals("/")) {
                OrderHeader order = gson.fromJson(reader, OrderHeader.class);
                conn.setAutoCommit(false); // Start Transaction

                // Insert Header
                String sqlOrder = "INSERT INTO Order_Header (order_date, customer_id, shipping_contact_mech_id, billing_contact_mech_id) VALUES (?, ?, ?, ?)";
                PreparedStatement stmtOrder = conn.prepareStatement(sqlOrder, Statement.RETURN_GENERATED_KEYS);
                // Date Fix: Convert String to SQL Date
                stmtOrder.setDate(1, java.sql.Date.valueOf(order.getOrderDate()));
                stmtOrder.setInt(2, order.getCustomerId());
                stmtOrder.setInt(3, order.getShippingContactMechId());
                stmtOrder.setInt(4, order.getBillingContactMechId());
                stmtOrder.executeUpdate();

                ResultSet rs = stmtOrder.getGeneratedKeys();
                int newOrderId = 0;
                if (rs.next()) newOrderId = rs.getInt(1);
                order.setOrderId(newOrderId);

                // Insert Items
                if (order.getOrderItems() != null) {
                    String sqlItem = "INSERT INTO Order_Item (order_id, product_id, quantity, status) VALUES (?, ?, ?, ?)";
                    PreparedStatement stmtItem = conn.prepareStatement(sqlItem);
                    for (OrderItem item : order.getOrderItems()) {
                        stmtItem.setInt(1, newOrderId);
                        stmtItem.setInt(2, item.getProductId());
                        stmtItem.setInt(3, item.getQuantity());
                        stmtItem.setString(4, item.getStatus());
                        stmtItem.addBatch();
                    }
                    stmtItem.executeBatch();
                }
                conn.commit(); // Commit Transaction
                resp.setStatus(201);
                out.print(gson.toJson(order));

            } 
            // Scenario 4: Add Item to Existing Order (Path: /1/items)
            else if (pathInfo.matches("/\\d+/items")) {
                int orderId = Integer.parseInt(pathInfo.split("/")[1]);
                OrderItem item = gson.fromJson(reader, OrderItem.class);
                
                String sql = "INSERT INTO Order_Item (order_id, product_id, quantity, status) VALUES (?, ?, ?, ?)";
                PreparedStatement stmt = conn.prepareStatement(sql);
                stmt.setInt(1, orderId);
                stmt.setInt(2, item.getProductId());
                stmt.setInt(3, item.getQuantity());
                stmt.setString(4, item.getStatus()); 
                stmt.executeUpdate();
                
                resp.setStatus(201);
                out.print("{\"message\": \"Item Added\"}");
            }
        } catch (Exception e) {
            e.printStackTrace();
            resp.setStatus(500);
            out.print("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }

    // ------------------- GET: Retrieve Order -------------------
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        PrintWriter out = resp.getWriter();
        String pathInfo = req.getPathInfo();
        
        if (pathInfo == null || pathInfo.equals("/")) return;
        int orderId = Integer.parseInt(pathInfo.split("/")[1]);

        try (Connection conn = DBConnection.getConnection()) {
            String sql = "SELECT * FROM Order_Header WHERE order_id = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, orderId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                OrderHeader order = new OrderHeader();
                order.setOrderId(rs.getInt("order_id"));
                // Date Fix: Convert SQL Date back to String
                order.setOrderDate(rs.getDate("order_date").toString());
                order.setCustomerId(rs.getInt("customer_id"));
                order.setShippingContactMechId(rs.getInt("shipping_contact_mech_id"));
                order.setBillingContactMechId(rs.getInt("billing_contact_mech_id"));

                String sqlItems = "SELECT * FROM Order_Item WHERE order_id = ?";
                PreparedStatement stmtItems = conn.prepareStatement(sqlItems);
                stmtItems.setInt(1, orderId);
                ResultSet rsItems = stmtItems.executeQuery();
                List<OrderItem> items = new ArrayList<>();
                while(rsItems.next()) {
                    OrderItem item = new OrderItem();
                    item.setOrderItemSeqId(rsItems.getInt("order_item_seq_id"));
                    item.setProductId(rsItems.getInt("product_id"));
                    item.setQuantity(rsItems.getInt("quantity"));
                    item.setStatus(rsItems.getString("status"));
                    items.add(item);
                }
                order.setOrderItems(items);
                out.print(gson.toJson(order));
            } else {
                resp.setStatus(404);
                out.print("{\"error\": \"Order Not Found\"}");
            }
        } catch (Exception e) { resp.setStatus(500); }
    }

    // ------------------- PUT: Update Order OR Update Item -------------------
    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String pathInfo = req.getPathInfo();
        BufferedReader reader = req.getReader();
        resp.setContentType("application/json");
        PrintWriter out = resp.getWriter();
        
        try (Connection conn = DBConnection.getConnection()) {
            // Scenario 3: Update Item Quantity (Path: /1/items/5)
            if (pathInfo.matches("/\\d+/items/\\d+")) {
                String[] parts = pathInfo.split("/");
                int seqId = Integer.parseInt(parts[3]); 
                
                OrderItem updateData = gson.fromJson(reader, OrderItem.class);
                String sql = "UPDATE Order_Item SET quantity = ? WHERE order_item_seq_id = ?";
                PreparedStatement stmt = conn.prepareStatement(sql);
                stmt.setInt(1, updateData.getQuantity());
                stmt.setInt(2, seqId);
                stmt.executeUpdate();
                resp.setStatus(200);
                out.print("{\"message\": \"Item quantity updated\"}");
            }
            // Update Order Address (Path: /1)
            else if (pathInfo.matches("/\\d+")) {
                int orderId = Integer.parseInt(pathInfo.substring(1));
                OrderHeader updateData = gson.fromJson(reader, OrderHeader.class);
                
                String sql = "UPDATE Order_Header SET shipping_contact_mech_id = ?, billing_contact_mech_id = ? WHERE order_id = ?";
                PreparedStatement stmt = conn.prepareStatement(sql);
                stmt.setInt(1, updateData.getShippingContactMechId());
                stmt.setInt(2, updateData.getBillingContactMechId());
                stmt.setInt(3, orderId);
                
                int rows = stmt.executeUpdate();
                if (rows > 0) {
                    resp.setStatus(200);
                    out.print("{\"message\": \"Order addresses updated\"}");
                } else {
                    resp.setStatus(404);
                    out.print("{\"error\": \"Order not found\"}");
                }
            }
        } catch (Exception e) { 
            e.printStackTrace();
            resp.setStatus(500); 
        }
    }

    // ------------------- DELETE: Delete Order OR Delete Item -------------------
    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String pathInfo = req.getPathInfo();
        try (Connection conn = DBConnection.getConnection()) {
            
            // Scenario 6: Delete Order (Path: /1)
            if (pathInfo.matches("/\\d+")) {
                int orderId = Integer.parseInt(pathInfo.substring(1));
                // Cascade delete handles items automatically
                String sql = "DELETE FROM Order_Header WHERE order_id = ?";
                PreparedStatement stmt = conn.prepareStatement(sql);
                stmt.setInt(1, orderId);
                stmt.executeUpdate();
                resp.setStatus(200);
            }
            // Scenario 5: Delete Item (Path: /1/items/5)
            else if (pathInfo.matches("/\\d+/items/\\d+")) {
                String[] parts = pathInfo.split("/");
                int seqId = Integer.parseInt(parts[3]);
                String sql = "DELETE FROM Order_Item WHERE order_item_seq_id = ?";
                PreparedStatement stmt = conn.prepareStatement(sql);
                stmt.setInt(1, seqId);
                stmt.executeUpdate();
                resp.setStatus(200);
            }
        } catch (Exception e) { resp.setStatus(500); }
    }
}