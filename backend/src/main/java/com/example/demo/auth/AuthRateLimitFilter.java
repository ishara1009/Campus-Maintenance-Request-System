package com.example.demo.auth;
import jakarta.servlet.*;import jakarta.servlet.http.*;import org.springframework.stereotype.Component;import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;import java.time.*;import java.util.concurrent.*;import java.util.concurrent.atomic.AtomicInteger;
@Component
public class AuthRateLimitFilter extends OncePerRequestFilter{
 private record Window(Instant started,AtomicInteger count){}private final ConcurrentMap<String,Window> attempts=new ConcurrentHashMap<>();
 protected boolean shouldNotFilter(HttpServletRequest r){String p=r.getRequestURI();return !r.getMethod().equals("POST")||!(p.equals("/api/auth/signin")||p.equals("/api/auth/signup")||p.equals("/api/auth/forgot-password"));}
 protected void doFilterInternal(HttpServletRequest req,HttpServletResponse res,FilterChain chain)throws ServletException,IOException{String key=req.getRemoteAddr()+":"+req.getRequestURI();Window w=attempts.compute(key,(k,old)->old==null||old.started().isBefore(Instant.now().minusSeconds(60))?new Window(Instant.now(),new AtomicInteger(1)):new Window(old.started(),new AtomicInteger(old.count().incrementAndGet())));if(w.count().get()>10){res.setStatus(429);res.setContentType("application/json");res.getWriter().write("{\"message\":\"Too many attempts. Please wait a minute.\"}");return;}chain.doFilter(req,res);}
}
