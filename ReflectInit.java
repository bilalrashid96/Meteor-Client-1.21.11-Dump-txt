package meteordevelopment.meteorclient.utils;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import meteordevelopment.meteorclient.addons.AddonManager;
import meteordevelopment.meteorclient.addons.MeteorAddon;
import org.reflections.Reflections;
import org.reflections.scanners.Scanner;
import org.reflections.scanners.Scanners;

public class ReflectInit {
   private static final List<Reflections> reflections = new ArrayList();

   private ReflectInit() {
   }

   public static void registerPackages() {
      for(MeteorAddon addon : AddonManager.ADDONS) {
         try {
            add(addon);
         } catch (AbstractMethodError e) {
            throw new RuntimeException("Addon \"%s\" is too old and cannot be ran.".formatted(addon.name), e);
         }
      }

   }

   private static void add(MeteorAddon addon) {
      String pkg = addon.getPackage();
      if (pkg != null && !pkg.isBlank()) {
         reflections.add(new Reflections(pkg, new Scanner[]{Scanners.MethodsAnnotated}));
      }
   }

   public static void init(Class<? extends Annotation> annotation) {
      for(Reflections reflection : reflections) {
         Set<Method> initTasks = reflection.getMethodsAnnotatedWith(annotation);
         if (initTasks == null) {
            return;
         }

         Map<Class<?>, List<Method>> byClass = (Map)initTasks.stream().collect(Collectors.groupingBy(Method::getDeclaringClass));
         Set<Method> left = new HashSet(initTasks);

         Method m;
         while((m = (Method)left.stream().findAny().orElse((Object)null)) != null) {
            reflectInit(m, annotation, left, byClass);
         }
      }

   }

   private static <T extends Annotation> void reflectInit(Method task, Class<T> annotation, Set<Method> left, Map<Class<?>, List<Method>> byClass) {
      left.remove(task);

      for(Class<?> clazz : getDependencies(task, annotation)) {
         for(Method m : (List)byClass.getOrDefault(clazz, Collections.emptyList())) {
            if (left.contains(m)) {
               reflectInit(m, annotation, left, byClass);
            }
         }
      }

      try {
         task.invoke((Object)null);
      } catch (InvocationTargetException | IllegalAccessException e) {
         throw new IllegalStateException("Error running @%s task '%s.%s'".formatted(annotation.getSimpleName(), task.getDeclaringClass().getSimpleName(), task.getName()), e);
      } catch (NullPointerException e) {
         throw new RuntimeException("Method \"%s\" using Init annotations from non-static context".formatted(task.getName()), e);
      }
   }

   private static <T extends Annotation> Class<?>[] getDependencies(Method task, Class<T> annotation) {
      T init = task.getAnnotation(annotation);
      Objects.requireNonNull(init);
      byte var4 = 0;
      Class[] var10000;
      //$FF: var4->value
      //0->meteordevelopment/meteorclient/utils/PreInit
      //1->meteordevelopment/meteorclient/utils/PostInit
      switch (init.typeSwitch<invokedynamic>(init, var4)) {
         case 0:
            PreInit pre = (PreInit)init;
            var10000 = pre.dependencies();
            break;
         case 1:
            PostInit post = (PostInit)init;
            var10000 = post.dependencies();
            break;
         default:
            var10000 = new Class[0];
      }

      return var10000;
   }
}
