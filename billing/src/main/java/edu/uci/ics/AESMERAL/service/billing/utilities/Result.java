package edu.uci.ics.AESMERAL.service.billing.utilities;

import javax.ws.rs.core.Response.Status;

public enum Result {
    INIT                    (0, "Just to fill in.", Status.OK),

    JSON_PARSE_EXCEPTION    (-3, "JSON Parse Exception.", Status.BAD_REQUEST),
    JSON_MAPPING_EXCEPTION  (-2,"JSON Parse Exception." , Status.BAD_REQUEST),
    INTERNAL_SERVER_ERROR   (-1, "Internal Server Error.", Status.INTERNAL_SERVER_ERROR),
    OPERATION_FAILED        (3150, "Shopping cart operation failed.", Status.OK),

    USER_NOT_FOUND          (14,"User not found.",Status.OK),
    DUPLICATE_INSERT        (311,"Duplicate insertion.",Status.OK),
    INSERT_SUCCESSFUL       (3100,"Shopping cart item inserted successfully.",Status.OK),

    INVALID_VALUE           (33,"Quantity has invalid value.", Status.OK),
    CART_NO_EXIST           (312,"Shopping cart item does not exist.", Status.OK),
    UPDATED_SUCCESSFUL      (3110, "Shopping cart item updated successfully.", Status.OK),

    DELETE_SUCCESSFUL       (3120, "Shopping cart item deleted successfully.", Status.OK),

    RETRIEVE_SUCCESSFUL     (3130, "Shopping cart retrieved successfully.",Status.OK),

    CLEARED_SUCCESSFUL      (3140, "Shopping cart cleared successfully.",Status.OK),

    ORDER_FAILED            (342,"Order creation failed.", Status.OK),
    ORDER_PLACED            (3400, "Order placed successfully.",Status.OK),

    ORDER_HIST_NO_EXIST     (313,"Order history does not exist.", Status.OK),
    ORDERS_RETRIEVED        (3410, "Orders retrieved successfully.", Status.OK),

    ORDER_COMPLETE          (3420, "Order is completed successfully.", Status.OK),
    NO_TOKEN                (3421, "Token not found.", Status.OK),
    ORDER_NO_COMPLETE       (3422,"Order can not be completed.", Status.OK),

    DISCOUNT_CREATED        (3200, "Discount code successfully created.", Status.OK),
    DISCOUNT_FAIL           (3210, "Unable to create discount code.", Status.OK),

    DISCOUNT_APPLIED        (3600, "Discount code was successfully applied.", Status.OK),
    DISCOUNT_INVALID        (3610, "Discount code is invalid.", Status.OK),
    DISCOUNT_EXPIRED        (3620, "Discount code is expired.",Status.OK),
    DISCOUNT_EXCEEDED       (3630, "Discount code limit exceeded", Status.OK),
    DISCOUNT_UNABLE         (3640, "Unable to apply discount code.", Status.OK);

    private final int resultCode;
    private final String message;
    private final Status status;

    Result(int resultCode, String message, Status status) {
        this.resultCode = resultCode;
        this.message = message;
        this.status = status;
    }

    public int getResultCode() {
        return resultCode;
    }

    public String getMessage() {
        return message;
    }

    public Status getStatus() {
        return status;
    }

}
