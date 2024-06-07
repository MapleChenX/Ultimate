1 使用rabbitmq发消息的时候记得消息是String类型的，要提前转，不然报错找都找不到
2 对象转JSON： String jsonString = JSON.toJSONString(emailAndCode);
3 JSON转对象： EmailAndCode emailAndCode = JSON.parseObject(jsonString, EmailAndCode.class);
4 日期转可认日期： 
    long timestamp = System.currentTimeMillis();
    System.out.println(new Timestamp(timestamp));
5 UUID生成并转为String： UUID.randomUUID().toString().replace("-", "");
6 String转int： Integer.parseInt(str)
7 所谓枚举，其实就是个预定义类，就不用new了，直接可以用，这样表示信息更加清晰，可读性更高
8 service层：this.getOne(query);
9   git remote add origin https://github.com/MapleChenX/Large_Social_PlatForm.git #添加远程地址，origin是远程地址的别名
    git branch -M main #没有main则创建，有main则推进
    git push -u origin main #-u表示将origin和本地main分支关联起来，之后只用git push就可以了，会自动识别的
10 mq的作用：异步通信，解耦，削峰
11 redis的作用：缓存，限时，计数器
12 校验：@Email @Pattern @Length @NotBlank @NotNull @NotEmpty @Size
13 签发JWT令牌： base64加密header和payload signature是对  header(base64加密)+ "." +payload(base64加密)  用秘钥进行hash加密之后的结果
14 跨域：其实就是因为不同源导致后端返回的数据被浏览器拦截不返回给前端，本来的目的是保证前端不会接受到恶意的数据，但现在前后端分离之后我们就必须要解决这个问题
    解决办法： 就是给HttpResponse设置好头部信息，告诉浏览器这个请求是允许的，不是恶意的
            Access-Control-Allow-Origin:  #返回给前端的地址，如果我后端都知道前端的地址了，你浏览器就没理由不让我返回给你了，毕竟我都知道你家门口了
            Access-Control-Allow-Methods: POST, GET, OPTIONS, DELETE, PUT #允许的请求方法
            Access-Control-Allow-Headers: Content-Type #允许的请求头
            Access-Control-Max-Age: 1800 #浏览器看见之后就会在该有效期内不再管当前对后端的跨域
            Access-Control-Allow-Credentials: true #是否允许携带cookie
15 SpringSecurity的登录接口要用Application/x-www-form-urlencoded，不能用Application/json格式，不然会报错；或者也可以直接塞url上
16 过滤器优先级： CORS > FLOW_LIMIT
17 Long value = Optional.ofNullable(value).orElse(0L); //如果value为空则返回0，存在则返回value