package utilities;

/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

public class ComputeMessageDifference {



  // Difference
  //-----------------------------------------------------------------------
  /**
   * Compares two Strings, and returns the portion where they differ.
   * (More precisely, return the remainder of the second String,
   * starting from where it's different from the first.)
   *
   * For example,
   * <code>difference("i am a machine", "i am a robot") -> "robot"</code>.
   *
   * <pre>
   * StringUtils.difference(null, null) = null
   * StringUtils.difference("", "") = ""
   * StringUtils.difference("", "abc") = "abc"
   * StringUtils.difference("abc", "") = ""
   * StringUtils.difference("abc", "abc") = ""
   * StringUtils.difference("ab", "abxyz") = "xyz"
   * StringUtils.difference("abcde", "abxyz") = "xyz"
   * StringUtils.difference("abcde", "xyz") = "xyz"
   * </pre>
   *
   * @param str1  the first String, may be null
   * @param str2  the second String, may be null
   * @return the portion of str2 where it differs from str1; returns the
   * empty String if they are equal
   * @since 2.0
   */
  public static String difference(String str1, String str2) {
      if (str1 == null) {
          return str2;
      }
      if (str2 == null) {
          return str1;
      }
      int at = indexOfDifference(str1, str2);
      if (at == -1) {
          return "";
      }
      return str2.substring(at);
  }
  /**
   * Compares two Strings, and returns the index at which the
   * Strings begin to differ.
   *
   * For example,
   * <code>indexOfDifference("i am a machine", "i am a robot") -> 7</code>
   *
   * <pre>
   * StringUtils.indexOfDifference(null, null) = -1
   * StringUtils.indexOfDifference("", "") = -1
   * StringUtils.indexOfDifference("", "abc") = 0
   * StringUtils.indexOfDifference("abc", "") = 0
   * StringUtils.indexOfDifference("abc", "abc") = -1
   * StringUtils.indexOfDifference("ab", "abxyz") = 2
   * StringUtils.indexOfDifference("abcde", "abxyz") = 2
   * StringUtils.indexOfDifference("abcde", "xyz") = 0
   * </pre>
   *
   * @param str1  the first String, may be null
   * @param str2  the second String, may be null
   * @return the index where str2 and str1 begin to differ; -1 if they are equal
   * @since 2.0
   */
  public static int indexOfDifference(String str1, String str2) {
      if (str1 == str2) {
          return -1;
      }
      if (str1 == null || str2 == null) {
          return 0;
      }
      int i;
      for (i = 0; i < str1.length() && i < str2.length(); ++i) {
          if (str1.charAt(i) != str2.charAt(i)) {
              break;
          }
      }
      if (i < str2.length() || i < str1.length()) {
          return i;
      }
      return -1;
  }

}
