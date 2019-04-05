package net.aldar.cramello.services;

public interface OnSmsReceivedListener {
    public void onSmsReceived(String sender, String message);
}
