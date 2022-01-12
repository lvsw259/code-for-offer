import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Iterator;
import java.util.Scanner;
import java.util.Set;


public class ServerSocketChannelTest {
    public static void main(String[] args) throws IOException {
        startServer();
    }
    public static void startServer() throws IOException {
        //1、获取选择器实例
        Selector selector = Selector.open();
        //2、获取通道实例
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        //3、设置为非阻塞
        serverSocketChannel.configureBlocking(false);
        //4、绑定地址
        serverSocketChannel.bind(new InetSocketAddress(8888));
        //5、将通道注册到选择器上，并指定监听事件为“接收就绪
        int i = serverSocketChannel.validOps();
        //System.out.println(i);//16,只能监听SelectionKey.OP_ACCEPT(接收到新连接事件)
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
        //轮询，选择感兴趣的IO就绪事件（选择键集合） select()会阻塞
        while (true) {
            // 监听ServerSocketChannel上的事件，每秒钟循环一次，
            // 这里select()方法返回的是当前监听得到的事件数目，为0表示当前没有任何事件到达
            if (selector.select(1000) == 0) {
//                System.out.println("has no message...");
                continue;
            }

            // 走到这里说明当前有监听的事件到达，获取所有监听的Channel所对应的SelectionKey对象，
            // 这里需要注意的是，前面我们已经将ServerSocketChannel注册到Selector中了，
            // 因而对于ServerSocketChannel，其监听得到的则是SelectionKey.OP_CONNECT事件。
            // 但是下面的代码中，我们也会将与客户端建立的连接Channel注册到Selector中，
            // 因而这里Selector中也会存在接收到的SelectionKey.OP_READ和OP_WRITE事件。
            Set<SelectionKey> selectionKeys = selector.selectedKeys();
            // 对监听到的事件进行遍历
            Iterator<SelectionKey> iterator = selectionKeys.iterator();
            while (iterator.hasNext()) {
                SelectionKey key = iterator.next();
                // 这里需要注意的是，Selector在为每个有事件到达的Channel建立SelectionKey对象
                // 之后，其并不会将其移除，如果我们不进行移除，那么下次循环时该事件还会再被处理一次，
                // 因而这里要调用remove()方法移除该SelectionKey
                iterator.remove();

                // 如果是有新的客户端Channel连接建立，则处理该事件
                if (key.isAcceptable()) {
                    accept(key, selector);
                }

                // 如果客户端连接中有可读取的数据，则处理该事件
                if (key.isReadable()) {
                    read(key);
                }

                // 如果可往客户端连接中写入数据，则处理该事件
                if (key.isValid() && key.isWritable()) {
                    write(key);
                }
            }
        }
    }

    private static void accept(SelectionKey key, Selector selector) throws IOException {
        // 这里由于只有ServerSocketChannel才会有客户端连接建立事件，因而这里可以直接将
        // Channel强转为ServerSocketChannel对象
        ServerSocketChannel serverChannel = (ServerSocketChannel) key.channel();
        // 获取客户端的连接
        SocketChannel socketChannel = serverChannel.accept();
        socketChannel.configureBlocking(false);
        // 将客户端连接Channel注册到Selector中，并且监听该Channel的OP_READ事件，
        // 也即等待客户端发送数据到服务器端
        socketChannel.register(selector, SelectionKey.OP_READ);
    }

    private static void read(SelectionKey key) throws IOException {
        // 这里只有客户端才会发送数据到服务器，因而可将其强转为SocketChannel对象
        SocketChannel clientChannel = (SocketChannel) key.channel();
        ByteBuffer buffer = ByteBuffer.wrap(new byte[1024]);
        // 从客户端Channel中读取数据，这里read()方法返回读取到的数据长度，
        // 如果为-1，则表示客户端断开连接了
        int len = clientChannel.read(buffer);
        if (len == -1) {
            clientChannel.close();
            return;
        }

        // 处理客户端数据
        System.out.println("**********server: read message**********");
        System.out.println(new String(buffer.array(), 0, len));
        // 由于已经读取了客户端数据，因而这里将对该Channel感兴趣的事件修改为
        // SelectionKey.OP_READ 和OP_WRITE，用于服务器往该Channel中写入数据
        key.interestOps(SelectionKey.OP_READ | SelectionKey.OP_WRITE);
    }

    private static void write(SelectionKey key) throws IOException {
        String message = "message from server";
        ByteBuffer buffer = ByteBuffer.wrap(message.getBytes());
        // 由于上面为客户端Channel设置了可供写入数据的事件，因而这里可以往客户端Channel写入数据
        SocketChannel clientChannel = (SocketChannel) key.channel();

        if (clientChannel.isOpen()) {
            System.out.println("**********server: write message**********");
            System.out.println(message);
            // 往客户端Channel写入数据
            clientChannel.write(buffer);
        }

        // 写入完成后，监听客户端会继续发送的数据
        if (!buffer.hasRemaining()) {
            key.interestOps(SelectionKey.OP_READ);
        }

        buffer.compact();
    }
}
