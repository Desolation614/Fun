/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.inject.Singleton
 *  net.runelite.api.Scene
 *  net.runelite.rlawt.AWTContext
 *  org.lwjgl.PointerBuffer
 *  org.lwjgl.opencl.APPLEGLSharing
 *  org.lwjgl.opencl.CL
 *  org.lwjgl.opencl.CL10
 *  org.lwjgl.opencl.CL10GL
 *  org.lwjgl.opencl.CL11
 *  org.lwjgl.opencl.CL12
 *  org.lwjgl.opencl.CLCapabilities
 *  org.lwjgl.opencl.CLContextCallback
 *  org.lwjgl.opencl.CLContextCallbackI
 *  org.lwjgl.opencl.CLImageFormat
 *  org.lwjgl.system.Configuration
 *  org.lwjgl.system.MemoryStack
 *  org.lwjgl.system.MemoryUtil
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package net.runelite.client.plugins.gpu;

import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.LongBuffer;
import java.nio.ShortBuffer;
import javax.inject.Singleton;
import net.runelite.api.Scene;
import net.runelite.client.plugins.gpu.GLBuffer;
import net.runelite.client.plugins.gpu.template.Template;
import net.runelite.client.util.OSType;
import net.runelite.rlawt.AWTContext;
import org.lwjgl.PointerBuffer;
import org.lwjgl.opencl.APPLEGLSharing;
import org.lwjgl.opencl.CL;
import org.lwjgl.opencl.CL10;
import org.lwjgl.opencl.CL10GL;
import org.lwjgl.opencl.CL11;
import org.lwjgl.opencl.CL12;
import org.lwjgl.opencl.CLCapabilities;
import org.lwjgl.opencl.CLContextCallback;
import org.lwjgl.opencl.CLContextCallbackI;
import org.lwjgl.opencl.CLImageFormat;
import org.lwjgl.system.Configuration;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Singleton
class OpenCLManager {
    private static final Logger log = LoggerFactory.getLogger(OpenCLManager.class);
    private static final String KERNEL_NAME_UNORDERED = "computeUnordered";
    private static final String KERNEL_NAME_LARGE = "computeLarge";
    private static final int MIN_WORK_GROUP_SIZE = 256;
    private static final int SMALL_SIZE = 512;
    private static final int LARGE_SIZE = 6144;
    private static final int SHARED_SIZE = 43;
    private boolean initialized;
    private int largeFaceCount;
    private int smallFaceCount;
    private long device;
    long context;
    private long commandQueue;
    private long programUnordered;
    private long programSmall;
    private long programLarge;
    private long kernelUnordered;
    private long kernelSmall;
    private long kernelLarge;
    private long tileHeightImage;

    OpenCLManager() {
    }

    void init(AWTContext awtContext) {
        this.commandQueue = 0L;
        this.context = 0L;
        this.device = 0L;
        this.programLarge = 0L;
        this.programSmall = 0L;
        this.programUnordered = 0L;
        this.kernelLarge = 0L;
        this.kernelSmall = 0L;
        this.kernelUnordered = 0L;
        this.tileHeightImage = 0L;
        CL.create();
        this.initialized = true;
        try (MemoryStack stack = MemoryStack.stackPush();){
            if (OSType.getOSType() == OSType.MacOS) {
                this.initContextMacOS(awtContext, stack);
            } else {
                this.initContext(awtContext, stack);
            }
            this.ensureMinWorkGroupSize();
            this.initQueue();
            this.compilePrograms(stack);
        }
    }

    void cleanup() {
        if (!this.initialized) {
            return;
        }
        try {
            if (this.tileHeightImage != 0L) {
                CL12.clReleaseMemObject((long)this.tileHeightImage);
            }
            CL12.clReleaseKernel((long)this.kernelUnordered);
            CL12.clReleaseKernel((long)this.kernelSmall);
            CL12.clReleaseKernel((long)this.kernelLarge);
            CL12.clReleaseProgram((long)this.programUnordered);
            CL12.clReleaseProgram((long)this.programSmall);
            CL12.clReleaseProgram((long)this.programLarge);
            CL12.clReleaseCommandQueue((long)this.commandQueue);
            CL12.clReleaseContext((long)this.context);
            CL12.clReleaseDevice((long)this.device);
        }
        finally {
            CL.destroy();
            this.initialized = false;
        }
    }

    private void initContext(AWTContext awtContext, MemoryStack stack) {
        IntBuffer pi = stack.mallocInt(1);
        OpenCLManager.checkCLError(CL11.clGetPlatformIDs(null, (IntBuffer)pi));
        if (pi.get(0) == 0) {
            throw new RuntimeException("No OpenCL platforms found.");
        }
        PointerBuffer platforms = stack.mallocPointer(pi.get(0));
        OpenCLManager.checkCLError(CL11.clGetPlatformIDs((PointerBuffer)platforms, (IntBuffer)null));
        PointerBuffer ctxProps = stack.mallocPointer(7);
        if (OSType.getOSType() == OSType.Windows) {
            ctxProps.put(4228L).put(0L).put(8200L).put(awtContext.getGLContext()).put(8203L).put(awtContext.getWGLHDC()).put(0L).flip();
        } else if (OSType.getOSType() == OSType.Linux) {
            ctxProps.put(4228L).put(0L).put(8200L).put(awtContext.getGLContext()).put(8202L).put(awtContext.getGLXDisplay()).put(0L).flip();
        } else {
            throw new RuntimeException("unsupported platform");
        }
        for (int p = 0; p < platforms.capacity(); ++p) {
            long platform = platforms.get(p);
            ctxProps.put(1, platform);
            try {
                CLCapabilities platformCaps = CL.createPlatformCapabilities((long)platform);
                log.debug("Platform profile: {}", (Object)OpenCLManager.getPlatformInfoStringUTF8(platform, 2304));
                log.debug("Platform version: {}", (Object)OpenCLManager.getPlatformInfoStringUTF8(platform, 2305));
                log.debug("Platform name: {}", (Object)OpenCLManager.getPlatformInfoStringUTF8(platform, 2306));
                log.debug("Platform vendor: {}", (Object)OpenCLManager.getPlatformInfoStringUTF8(platform, 2307));
                log.debug("Platform extensions: {}", (Object)OpenCLManager.getPlatformInfoStringUTF8(platform, 2308));
                OpenCLManager.checkCLError(CL11.clGetDeviceIDs((long)platform, (long)4L, null, (IntBuffer)pi));
                PointerBuffer devices = stack.mallocPointer(pi.get(0));
                OpenCLManager.checkCLError(CL11.clGetDeviceIDs((long)platform, (long)4L, (PointerBuffer)devices, (IntBuffer)null));
                for (int d = 0; d < devices.capacity(); ++d) {
                    long device = devices.get(d);
                    try {
                        CLCapabilities deviceCaps = CL.createDeviceCapabilities((long)device, (CLCapabilities)platformCaps);
                        log.debug("Device id {}", (Object)device);
                        log.debug("\tCL_DEVICE_NAME: {}", (Object)OpenCLManager.getDeviceInfoStringUTF8(device, 4139));
                        log.debug("\tCL_DEVICE_VENDOR: {}", (Object)OpenCLManager.getDeviceInfoStringUTF8(device, 4140));
                        log.debug("\tCL_DRIVER_VERSION: {}", (Object)OpenCLManager.getDeviceInfoStringUTF8(device, 4141));
                        log.debug("\tCL_DEVICE_PROFILE: {}", (Object)OpenCLManager.getDeviceInfoStringUTF8(device, 4142));
                        log.debug("\tCL_DEVICE_VERSION: {}", (Object)OpenCLManager.getDeviceInfoStringUTF8(device, 4143));
                        log.debug("\tCL_DEVICE_EXTENSIONS: {}", (Object)OpenCLManager.getDeviceInfoStringUTF8(device, 4144));
                        log.debug("\tCL_DEVICE_TYPE: {}", (Object)OpenCLManager.getDeviceInfoLong(device, 4096));
                        log.debug("\tCL_DEVICE_VENDOR_ID: {}", (Object)OpenCLManager.getDeviceInfoInt(device, 4097));
                        log.debug("\tCL_DEVICE_MAX_COMPUTE_UNITS: {}", (Object)OpenCLManager.getDeviceInfoInt(device, 4098));
                        log.debug("\tCL_DEVICE_MAX_WORK_ITEM_DIMENSIONS: {}", (Object)OpenCLManager.getDeviceInfoInt(device, 4099));
                        log.debug("\tCL_DEVICE_MAX_WORK_GROUP_SIZE: {}", (Object)OpenCLManager.getDeviceInfoPointer(device, 4100));
                        log.debug("\tCL_DEVICE_MAX_CLOCK_FREQUENCY: {}", (Object)OpenCLManager.getDeviceInfoInt(device, 4108));
                        log.debug("\tCL_DEVICE_ADDRESS_BITS: {}", (Object)OpenCLManager.getDeviceInfoInt(device, 4109));
                        log.debug("\tCL_DEVICE_AVAILABLE: {}", (Object)(OpenCLManager.getDeviceInfoInt(device, 4135) != 0 ? 1 : 0));
                        log.debug("\tCL_DEVICE_COMPILER_AVAILABLE: {}", (Object)(OpenCLManager.getDeviceInfoInt(device, 4136) != 0 ? 1 : 0));
                        if (!deviceCaps.cl_khr_gl_sharing && !deviceCaps.cl_APPLE_gl_sharing) continue;
                        IntBuffer errcode_ret = stack.callocInt(1);
                        long context = CL11.clCreateContext((PointerBuffer)ctxProps, (long)device, (CLContextCallbackI)CLContextCallback.create((errinfo, private_info, cb, user_data) -> log.error("[LWJGL] cl_context_callback: {}", (Object)MemoryUtil.memUTF8((long)errinfo))), (long)0L, (IntBuffer)errcode_ret);
                        OpenCLManager.checkCLError(errcode_ret);
                        this.device = device;
                        this.context = context;
                        return;
                    }
                    catch (Exception ex) {
                        log.error("error checking device", (Throwable)ex);
                    }
                }
                continue;
            }
            catch (Exception ex) {
                log.error("error checking platform", (Throwable)ex);
            }
        }
        throw new RuntimeException("Unable to find compute platform");
    }

    private void initContextMacOS(AWTContext awtContext, MemoryStack stack) {
        PointerBuffer ctxProps = stack.mallocPointer(3);
        ctxProps.put(0x10000000L).put(awtContext.getCGLShareGroup()).put(0L).flip();
        IntBuffer errcode_ret = stack.callocInt(1);
        PointerBuffer devices = stack.mallocPointer(0);
        long context = CL11.clCreateContext((PointerBuffer)ctxProps, (PointerBuffer)devices, (CLContextCallbackI)CLContextCallback.create((errinfo, private_info, cb, user_data) -> log.error("[LWJGL] cl_context_callback: {}", (Object)MemoryUtil.memUTF8((long)errinfo))), (long)0L, (IntBuffer)errcode_ret);
        OpenCLManager.checkCLError(errcode_ret);
        PointerBuffer deviceBuf = stack.mallocPointer(1);
        OpenCLManager.checkCLError(APPLEGLSharing.clGetGLContextInfoAPPLE((long)context, (long)awtContext.getGLContext(), (int)0x10000002, (PointerBuffer)deviceBuf, null));
        long device = deviceBuf.get(0);
        log.debug("Got macOS CLGL compute device {}", (Object)device);
        this.context = context;
        this.device = device;
    }

    private void ensureMinWorkGroupSize() {
        long[] maxWorkGroupSize = new long[1];
        CL12.clGetDeviceInfo((long)this.device, (int)4100, (long[])maxWorkGroupSize, null);
        log.debug("Device CL_DEVICE_MAX_WORK_GROUP_SIZE: {}", (Object)maxWorkGroupSize[0]);
        if (maxWorkGroupSize[0] < 256L) {
            throw new RuntimeException("Compute device does not support min work group size 256");
        }
        int groupSize = Integer.MIN_VALUE >>> Integer.numberOfLeadingZeros((int)maxWorkGroupSize[0]);
        this.largeFaceCount = 6144 / Math.min(groupSize, 6144);
        this.smallFaceCount = 512 / Math.min(groupSize, 512);
        log.debug("Face counts: small: {}, large: {}", (Object)this.smallFaceCount, (Object)this.largeFaceCount);
    }

    private void initQueue() {
        long[] l = new long[1];
        CL12.clGetDeviceInfo((long)this.device, (int)4138, (long[])l, null);
        this.commandQueue = CL12.clCreateCommandQueue((long)this.context, (long)this.device, (long)(l[0] & 1L), (int[])null);
        log.debug("Created command_queue {}, properties {}", (Object)this.commandQueue, (Object)(l[0] & 1L));
    }

    private long compileProgram(MemoryStack stack, String programSource) {
        log.trace("Compiling program:\n {}", (Object)programSource);
        IntBuffer errcode_ret = stack.callocInt(1);
        long program = CL12.clCreateProgramWithSource((long)this.context, (CharSequence)programSource, (IntBuffer)errcode_ret);
        OpenCLManager.checkCLError(errcode_ret);
        int err = CL12.clBuildProgram((long)program, (long)this.device, (CharSequence)"", null, (long)0L);
        if (err != 0) {
            String errstr = OpenCLManager.getProgramBuildInfoStringASCII(program, this.device, 4483);
            throw new RuntimeException(errstr);
        }
        log.debug("Build status: {}", (Object)OpenCLManager.getProgramBuildInfoInt(program, this.device, 4481));
        log.debug("Binary type: {}", (Object)OpenCLManager.getProgramBuildInfoInt(program, this.device, 4484));
        log.debug("Build options: {}", (Object)OpenCLManager.getProgramBuildInfoStringASCII(program, this.device, 4482));
        log.debug("Build log: {}", (Object)OpenCLManager.getProgramBuildInfoStringASCII(program, this.device, 4483));
        return program;
    }

    private long getKernel(MemoryStack stack, long program, String kernelName) {
        IntBuffer errcode_ret = stack.callocInt(1);
        long kernel = CL12.clCreateKernel((long)program, (CharSequence)kernelName, (IntBuffer)errcode_ret);
        OpenCLManager.checkCLError(errcode_ret);
        log.debug("Loaded kernel {} for program {}", (Object)kernelName, (Object)program);
        return kernel;
    }

    private void compilePrograms(MemoryStack stack) {
        Template templateSmall = new Template().addInclude(OpenCLManager.class).add(key -> key.equals("FACE_COUNT") ? "#define FACE_COUNT " + this.smallFaceCount : null);
        Template templateLarge = new Template().addInclude(OpenCLManager.class).add(key -> key.equals("FACE_COUNT") ? "#define FACE_COUNT " + this.largeFaceCount : null);
        String unordered = new Template().addInclude(OpenCLManager.class).load("comp_unordered.cl");
        String small = templateSmall.load("comp.cl");
        String large = templateLarge.load("comp.cl");
        this.programUnordered = this.compileProgram(stack, unordered);
        this.programSmall = this.compileProgram(stack, small);
        this.programLarge = this.compileProgram(stack, large);
        this.kernelUnordered = this.getKernel(stack, this.programUnordered, KERNEL_NAME_UNORDERED);
        this.kernelSmall = this.getKernel(stack, this.programSmall, KERNEL_NAME_LARGE);
        this.kernelLarge = this.getKernel(stack, this.programLarge, KERNEL_NAME_LARGE);
    }

    void uploadTileHeights(Scene scene) {
        if (this.tileHeightImage != 0L) {
            CL12.clReleaseMemObject((long)this.tileHeightImage);
            this.tileHeightImage = 0L;
        }
        int TILEHEIGHT_BUFFER_SIZE = 270848;
        ShortBuffer tileBuffer = MemoryUtil.memAllocShort((int)270848);
        int[][][] tileHeights = scene.getTileHeights();
        for (int z = 0; z < 4; ++z) {
            for (int y = 0; y < 184; ++y) {
                for (int x = 0; x < 184; ++x) {
                    int h = tileHeights[z][x][y];
                    assert ((h & 7) == 0);
                    tileBuffer.put((short)(h >>= 3));
                }
            }
        }
        tileBuffer.flip();
        try (MemoryStack stack = MemoryStack.stackPush();){
            CLImageFormat imageFormat = CLImageFormat.calloc((MemoryStack)stack);
            imageFormat.image_channel_order(4272);
            imageFormat.image_channel_data_type(4312);
            IntBuffer errcode_ret = stack.callocInt(1);
            this.tileHeightImage = CL12.clCreateImage3D((long)this.context, (long)36L, (CLImageFormat)imageFormat, (long)184L, (long)184L, (long)4L, (long)0L, (long)0L, (ShortBuffer)tileBuffer, (IntBuffer)errcode_ret);
            OpenCLManager.checkCLError(errcode_ret);
        }
        MemoryUtil.memFree((Buffer)tileBuffer);
    }

    void compute(int unorderedModels, int smallModels, int largeModels, GLBuffer sceneVertexBuffer, GLBuffer sceneUvBuffer, GLBuffer vertexBuffer, GLBuffer uvBuffer, GLBuffer unorderedBuffer, GLBuffer smallBuffer, GLBuffer largeBuffer, GLBuffer outVertexBuffer, GLBuffer outUvBuffer, GLBuffer uniformBuffer) {
        try (MemoryStack stack = MemoryStack.stackPush();){
            PointerBuffer glBuffers = stack.mallocPointer(10);
            glBuffers.put(sceneVertexBuffer.clBuffer);
            glBuffers.put(sceneUvBuffer.clBuffer);
            glBuffers.put(unorderedBuffer.clBuffer);
            glBuffers.put(smallBuffer.clBuffer);
            glBuffers.put(largeBuffer.clBuffer);
            glBuffers.put(vertexBuffer.clBuffer);
            glBuffers.put(uvBuffer.clBuffer);
            glBuffers.put(outVertexBuffer.clBuffer);
            glBuffers.put(outUvBuffer.clBuffer);
            glBuffers.put(uniformBuffer.clBuffer);
            glBuffers.flip();
            PointerBuffer acquireEvent = stack.mallocPointer(1);
            CL10GL.clEnqueueAcquireGLObjects((long)this.commandQueue, (PointerBuffer)glBuffers, null, (PointerBuffer)acquireEvent);
            PointerBuffer computeEvents = stack.mallocPointer(3);
            if (unorderedModels > 0) {
                CL12.clSetKernelArg1p((long)this.kernelUnordered, (int)0, (long)unorderedBuffer.clBuffer);
                CL12.clSetKernelArg1p((long)this.kernelUnordered, (int)1, (long)sceneVertexBuffer.clBuffer);
                CL12.clSetKernelArg1p((long)this.kernelUnordered, (int)2, (long)vertexBuffer.clBuffer);
                CL12.clSetKernelArg1p((long)this.kernelUnordered, (int)3, (long)sceneUvBuffer.clBuffer);
                CL12.clSetKernelArg1p((long)this.kernelUnordered, (int)4, (long)uvBuffer.clBuffer);
                CL12.clSetKernelArg1p((long)this.kernelUnordered, (int)5, (long)outVertexBuffer.clBuffer);
                CL12.clSetKernelArg1p((long)this.kernelUnordered, (int)6, (long)outUvBuffer.clBuffer);
                CL12.clEnqueueNDRangeKernel((long)this.commandQueue, (long)this.kernelUnordered, (int)1, null, (PointerBuffer)stack.pointers((long)unorderedModels * 6L), (PointerBuffer)stack.pointers(6L), (PointerBuffer)acquireEvent, (PointerBuffer)computeEvents);
                computeEvents.position(computeEvents.position() + 1);
            }
            if (smallModels > 0) {
                CL12.clSetKernelArg((long)this.kernelSmall, (int)0, (long)2220L);
                CL12.clSetKernelArg1p((long)this.kernelSmall, (int)1, (long)smallBuffer.clBuffer);
                CL12.clSetKernelArg1p((long)this.kernelSmall, (int)2, (long)sceneVertexBuffer.clBuffer);
                CL12.clSetKernelArg1p((long)this.kernelSmall, (int)3, (long)vertexBuffer.clBuffer);
                CL12.clSetKernelArg1p((long)this.kernelSmall, (int)4, (long)sceneUvBuffer.clBuffer);
                CL12.clSetKernelArg1p((long)this.kernelSmall, (int)5, (long)uvBuffer.clBuffer);
                CL12.clSetKernelArg1p((long)this.kernelSmall, (int)6, (long)outVertexBuffer.clBuffer);
                CL12.clSetKernelArg1p((long)this.kernelSmall, (int)7, (long)outUvBuffer.clBuffer);
                CL12.clSetKernelArg1p((long)this.kernelSmall, (int)8, (long)uniformBuffer.clBuffer);
                CL12.clSetKernelArg1l((long)this.kernelSmall, (int)9, (long)this.tileHeightImage);
                CL12.clEnqueueNDRangeKernel((long)this.commandQueue, (long)this.kernelSmall, (int)1, null, (PointerBuffer)stack.pointers((long)(smallModels * (512 / this.smallFaceCount))), (PointerBuffer)stack.pointers((long)(512 / this.smallFaceCount)), (PointerBuffer)acquireEvent, (PointerBuffer)computeEvents);
                computeEvents.position(computeEvents.position() + 1);
            }
            if (largeModels > 0) {
                CL12.clSetKernelArg((long)this.kernelLarge, (int)0, (long)24748L);
                CL12.clSetKernelArg1p((long)this.kernelLarge, (int)1, (long)largeBuffer.clBuffer);
                CL12.clSetKernelArg1p((long)this.kernelLarge, (int)2, (long)sceneVertexBuffer.clBuffer);
                CL12.clSetKernelArg1p((long)this.kernelLarge, (int)3, (long)vertexBuffer.clBuffer);
                CL12.clSetKernelArg1p((long)this.kernelLarge, (int)4, (long)sceneUvBuffer.clBuffer);
                CL12.clSetKernelArg1p((long)this.kernelLarge, (int)5, (long)uvBuffer.clBuffer);
                CL12.clSetKernelArg1p((long)this.kernelLarge, (int)6, (long)outVertexBuffer.clBuffer);
                CL12.clSetKernelArg1p((long)this.kernelLarge, (int)7, (long)outUvBuffer.clBuffer);
                CL12.clSetKernelArg1p((long)this.kernelLarge, (int)8, (long)uniformBuffer.clBuffer);
                CL12.clSetKernelArg1l((long)this.kernelLarge, (int)9, (long)this.tileHeightImage);
                CL12.clEnqueueNDRangeKernel((long)this.commandQueue, (long)this.kernelLarge, (int)1, null, (PointerBuffer)stack.pointers((long)(largeModels * (6144 / this.largeFaceCount))), (PointerBuffer)stack.pointers((long)(6144 / this.largeFaceCount)), (PointerBuffer)acquireEvent, (PointerBuffer)computeEvents);
                computeEvents.position(computeEvents.position() + 1);
            }
            if (computeEvents.position() == 0) {
                CL10GL.clEnqueueReleaseGLObjects((long)this.commandQueue, (PointerBuffer)glBuffers, null, null);
            } else {
                computeEvents.flip();
                CL10GL.clEnqueueReleaseGLObjects((long)this.commandQueue, (PointerBuffer)glBuffers, (PointerBuffer)computeEvents, null);
            }
        }
    }

    void finish() {
        CL12.clFinish((long)this.commandQueue);
    }

    private static String getPlatformInfoStringUTF8(long cl_platform_id, int param_name) {
        try (MemoryStack stack = MemoryStack.stackPush();){
            PointerBuffer pp = stack.mallocPointer(1);
            OpenCLManager.checkCLError(CL10.clGetPlatformInfo((long)cl_platform_id, (int)param_name, (ByteBuffer)null, (PointerBuffer)pp));
            int bytes = (int)pp.get(0);
            ByteBuffer buffer = stack.malloc(bytes);
            OpenCLManager.checkCLError(CL10.clGetPlatformInfo((long)cl_platform_id, (int)param_name, (ByteBuffer)buffer, null));
            String string = MemoryUtil.memUTF8((ByteBuffer)buffer, (int)(bytes - 1));
            return string;
        }
    }

    private static long getDeviceInfoLong(long cl_device_id, int param_name) {
        try (MemoryStack stack = MemoryStack.stackPush();){
            LongBuffer pl = stack.mallocLong(1);
            OpenCLManager.checkCLError(CL11.clGetDeviceInfo((long)cl_device_id, (int)param_name, (LongBuffer)pl, null));
            long l = pl.get(0);
            return l;
        }
    }

    private static int getDeviceInfoInt(long cl_device_id, int param_name) {
        try (MemoryStack stack = MemoryStack.stackPush();){
            IntBuffer pl = stack.mallocInt(1);
            OpenCLManager.checkCLError(CL11.clGetDeviceInfo((long)cl_device_id, (int)param_name, (IntBuffer)pl, null));
            int n = pl.get(0);
            return n;
        }
    }

    private static long getDeviceInfoPointer(long cl_device_id, int param_name) {
        try (MemoryStack stack = MemoryStack.stackPush();){
            PointerBuffer pp = stack.mallocPointer(1);
            OpenCLManager.checkCLError(CL11.clGetDeviceInfo((long)cl_device_id, (int)param_name, (PointerBuffer)pp, null));
            long l = pp.get(0);
            return l;
        }
    }

    private static String getDeviceInfoStringUTF8(long cl_device_id, int param_name) {
        try (MemoryStack stack = MemoryStack.stackPush();){
            PointerBuffer pp = stack.mallocPointer(1);
            OpenCLManager.checkCLError(CL11.clGetDeviceInfo((long)cl_device_id, (int)param_name, (ByteBuffer)null, (PointerBuffer)pp));
            int bytes = (int)pp.get(0);
            ByteBuffer buffer = stack.malloc(bytes);
            OpenCLManager.checkCLError(CL11.clGetDeviceInfo((long)cl_device_id, (int)param_name, (ByteBuffer)buffer, null));
            String string = MemoryUtil.memUTF8((ByteBuffer)buffer, (int)(bytes - 1));
            return string;
        }
    }

    private static int getProgramBuildInfoInt(long cl_program_id, long cl_device_id, int param_name) {
        try (MemoryStack stack = MemoryStack.stackPush();){
            IntBuffer pl = stack.mallocInt(1);
            OpenCLManager.checkCLError(CL10.clGetProgramBuildInfo((long)cl_program_id, (long)cl_device_id, (int)param_name, (IntBuffer)pl, null));
            int n = pl.get(0);
            return n;
        }
    }

    private static String getProgramBuildInfoStringASCII(long cl_program_id, long cl_device_id, int param_name) {
        try (MemoryStack stack = MemoryStack.stackPush();){
            PointerBuffer pp = stack.mallocPointer(1);
            OpenCLManager.checkCLError(CL10.clGetProgramBuildInfo((long)cl_program_id, (long)cl_device_id, (int)param_name, (ByteBuffer)null, (PointerBuffer)pp));
            int bytes = (int)pp.get(0);
            ByteBuffer buffer = stack.malloc(bytes);
            OpenCLManager.checkCLError(CL10.clGetProgramBuildInfo((long)cl_program_id, (long)cl_device_id, (int)param_name, (ByteBuffer)buffer, null));
            String string = MemoryUtil.memASCII((ByteBuffer)buffer, (int)(bytes - 1));
            return string;
        }
    }

    private static void checkCLError(IntBuffer errcode) {
        OpenCLManager.checkCLError(errcode.get(errcode.position()));
    }

    private static void checkCLError(int errcode) {
        if (errcode != 0) {
            throw new RuntimeException(String.format("OpenCL error [%d]", errcode));
        }
    }

    static {
        Configuration.OPENCL_EXPLICIT_INIT.set((Object)true);
    }
}

