package mekanism.common.inventory.container;

public interface ISecurityContainer {

    /**
     * @apiNote Only for use on the server, which means that it doesn't need to properly update on the client side if the stack changes
     */

    Object getSecurityObject();
}