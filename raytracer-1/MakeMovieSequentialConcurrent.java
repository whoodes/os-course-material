import raytracer.ABunchOfSpheres;

import java.lang.ArrayIndexOutOfBoundsException;
import java.lang.NumberFormatException;

public class MakeMovieSequentialConcurrent {
    private static int num_frames = 0;
    private static int num_threads = 1;
    private ABunchOfSpheres movie = new ABunchOfSpheres();

    public void threadify(Thread[] threads, int split) {
        switch (split) {
            case 1:
                threads[0] = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        for (int count = 0; count < num_frames; count++) {
                            movie.render_scene("./frame_" + String.format("%05d", count) + ".png", count);
                        }
                    }
                });
                threads[0].start();

                try {
                    threads[0].join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                break;
            case 2:
                int ratio = num_frames / split;

                /* 1/2 */
                threads[0] = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        for (int count = 0; count < ratio; count++) {
                            movie.render_scene("./frame_" + String.format("%05d", count) + ".png", count);
                        }
                    }
                });
                threads[0].start();

                /* 2/2 */
                threads[1] = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        for (int count = ratio; count < num_frames; count++) {
                            movie.render_scene("./frame_" + String.format("%05d", count) + ".png", count);
                        }
                    }
                });
                threads[1].start();

                try {
                    threads[0].join();
                    threads[1].join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                break;
            case 3:
                ratio = num_threads / split;

                /* 1/3 */
                threads[0] = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        for (int count = 0; count < ratio; count++) {
                            movie.render_scene("./frame_" + String.format("%05d", count) + ".png", count);
                        }
                    }
                });
                threads[0].start();

                /* 2/3 */
                threads[1] = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        for (int count = ratio; count < ratio * 2; count++) {
                            movie.render_scene("./frame_" + String.format("%05d", count) + ".png", count);
                        }
                    }
                });
                threads[1].start();

                /* 3/3 */
                threads[2] = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        for (int count = ratio * 2; count < num_frames; count++) {
                            movie.render_scene("./frame_" + String.format("%05d", count) + ".png", count);
                        }
                    }
                });
                threads[2].start();

                try {
                    threads[0].join();
                    threads[1].join();
                    threads[2].join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                break;
            case 4:
                ratio = num_frames / split;

                /* 1/4 */
                threads[0] = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        for (int count = 0; count < ratio; count++) {
                            movie.render_scene("./frame_" + String.format("%05d", count) + ".png", count);
                        }
                    }
                });
                threads[0].start();

                /* 2/4 */
                threads[1] = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        for (int count = ratio; count < ratio * 2; count++) {
                            movie.render_scene("./frame_" + String.format("%05d", count) + ".png", count);
                        }
                    }
                });
                threads[1].start();

                /* 3/4 */
                threads[2] = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        for (int count = ratio * 2; count < ratio * 3; count++) {
                            movie.render_scene("./frame_" + String.format("%05d", count) + ".png", count);
                        }
                    }
                });
                threads[2].start();

                /* 4/4 */
                threads[3] = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        for (int count = ratio * 3; count < num_frames; count++) {
                            movie.render_scene("./frame_" + String.format("%05d", count) + ".png", count);
                        }
                    }
                });
                threads[3].start();

                try {
                    threads[0].join();
                    threads[1].join();
                    threads[2].join();
                    threads[3].join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                break;
            default:
                return;
        }
    }

    public MakeMovieSequentialConcurrent(int num_frames, int num_threads) {
        Thread t1 = new Thread();
        Thread t2 = new Thread();
        Thread t3 = new Thread();
        Thread t4 = new Thread();
        Thread[] threads = {t1, t2, t3, t4};
        double start = System.currentTimeMillis();
        threadify(threads, num_threads);

        /* Calculate execution time */
        double end = System.currentTimeMillis();
        double total = (end - start) / 1000;
        System.out.println("\nExecution time: " + total + " seconds.");
    }

    private static void abort(String message) {
        System.err.println(message);
        System.exit(1);
    }

    public static void main(String[] args) {
        try {
            num_frames = Integer.parseInt(args[0]);
            if (args.length > 1) {
                num_threads = Integer.parseInt(args[1]);
            }
        } catch (NumberFormatException e) {
            abort("Invalid number of frames/threads (should be an integer)");
        } catch (ArrayIndexOutOfBoundsException e) {
            abort("Usage: java MakeMovieSequential <num of frames> <num of threads (default=1)>");
        }
        if (num_frames < 0 || num_threads < 1) {
            abort("Invalid number of frames (should be positive)");
        }
        if (num_threads < 1) {
            abort("Invalid number of frames (should be at least 1)");
        }
        if (num_threads > 4) {
            abort("Maximum threads should not exceed 4.");
        }
        new MakeMovieSequentialConcurrent(num_frames, num_threads);
    }
}
