package red.hermesapi.API;

import io.netty.bootstrap.ServerBootstrap;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.stream.ChunkedWriteHandler;


/**
 * Discards any incoming data.
 */
public class APIServer {

    private int port;

    // Initialise class with port
    public APIServer(int port) {
        this.port = port;
    }

    // Run method
    public void run() throws Exception {
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {

            ServerBootstrap b = new ServerBootstrap(); //Helper class to start server

            // Server configuration
            b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class) // Use nonblocking IO channel

                    // Configures how incoming data is processed
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        public void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline()
                            .addLast(new HttpServerCodec())
                            .addLast(new HttpObjectAggregator(1048576)) // Max 1MB HTTP body// Decodes HTTP requests
                            .addLast(new ChunkedWriteHandler())
                            .addLast(new SSEHandler())
                            .addLast(new APIHandler()); //create pipeline of data handling with APIHandler last
                        }
                    })
                    .option(ChannelOption.SO_BACKLOG, 128)
                    .childOption(ChannelOption.SO_KEEPALIVE, true);


            ChannelFuture f = b.bind(port).sync(); // (7)


            f.channel().closeFuture().sync();
        } finally {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }
    }
}