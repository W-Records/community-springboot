package com.example.userservice.pojo;

// 统一响应的数据,    封装结果类
public class Result {
//    前端在接收到这样的响应后，
//    可以轻松地根据 code 判断请求是否成功，
//    通过 message 展示相应提示信息，
//    从 data 中提取实际业务数据
    private Integer code; // 1 成功,0 失败
    private String msg; // 提示信息
    private Object data; // 返回的数据

    // 定义静态方法 作用快速构造Result对象
    // 成功
    public static Result succeed(){
        return new Result(1,"succeed",null);
    }
    public static Result succeed(Object data){
        return new Result(1,"succeed",data);
    }
    //失败
    public static Result error(String msg){
        return new Result(0,msg,null);
    }


    public Result() {
    }

    public Result(Integer code, String msg, Object data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    /**
     * 获取
     * @return code
     */
    public Integer getCode() {
        return code;
    }

    /**
     * 设置
     * @param code
     */
    public void setCode(Integer code) {
        this.code = code;
    }

    /**
     * 获取
     * @return msg
     */
    public String getMsg() {
        return msg;
    }

    /**
     * 设置
     * @param msg
     */
    public void setMsg(String msg) {
        this.msg = msg;
    }

    /**
     * 获取
     * @return data
     */
    public Object getData() {
        return data;
    }

    /**
     * 设置
     * @param data
     */
    public void setData(Object data) {
        this.data = data;
    }

    public String toString() {
        return "Result{code = " + code + ", msg = " + msg + ", data = " + data + "}";
    }
}
