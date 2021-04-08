/*
 * Copyright (c) 2021, Huawei Technologies Co., Ltd. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Oracle, 500 Oracle Parkway, Redwood Shores, CA 94065 USA
 * or visit www.oracle.com if you need additional information or have any
 * questions.
 */

/*
 * @test
 * @bug 8264760
 * @summary JVM crashes when two threads encounter the same resolution error
 *
 * @library /test/lib
 * @compile TestNestHostErrorWithMultiThread.java
 * @compile HostNoNestMember.jcod
 *
 * @run main/othervm TestNestHostErrorWithMultiThread
 */

import java.util.concurrent.CountDownLatch;

class HostNoNestMember {
  class Member {
    private int value;
  }

  public int foo() {
    Member m = new Member();
    return m.value;
  }
}

public class TestNestHostErrorWithMultiThread {

  public static void main(String args[]) {
    TestNestHostErrorWithMultiThread t = new TestNestHostErrorWithMultiThread();
    t.test();
  }

  public void test() {

    CountDownLatch latch = new CountDownLatch(1);

    new Thread(() -> {
      try {
        latch.await();
        HostNoNestMember h = new HostNoNestMember();
        h.foo();
      } catch (Exception e) {
        e.printStackTrace();
      }
    }).start();

    new Thread(() -> {
      try {
        latch.await();
        HostNoNestMember h = new HostNoNestMember();
        h.foo();
      } catch (Exception e) {
        e.printStackTrace();
      }
    }).start();

    latch.countDown();
  }
}
