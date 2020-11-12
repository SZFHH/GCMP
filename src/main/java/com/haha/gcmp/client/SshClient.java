package com.haha.gcmp.client;

/**
 * @author SZFHH
 * @date 2020/11/12
 */
public interface SshClient {
    /**
     * 执行shell命令
     *
     * @param cmd          命令
     * @param exceptionMsg 异常提示信息
     * @return 输出结果
     */
    String execShellCmd(String cmd, String exceptionMsg);
}
