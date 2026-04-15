/*
 * Copyright 2016, Lawrence Livermore National Security, LLC. 
 * All rights reserved
 * 
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.utility.xml.bind;

import static gov.llnl.utility.ClassUtilities.INTEGER_PRIMITIVE;
import gov.llnl.utility.TestSupport.TestPackage;
import gov.llnl.utility.TestSupport.TestReader;
import gov.llnl.utility.TestSupport.TestReaderMixed;
import gov.llnl.utility.TestSupport.TestSectionImpl;
import gov.llnl.utility.xml.bind.ReaderBuilderImpl.Producer;
import gov.llnl.utility.xml.bind.readers.PrimitiveReaderImpl;
import gov.llnl.utility.io.ReaderException;
import gov.llnl.utility.xml.bind.Reader.Option;
import gov.llnl.utility.xml.bind.Reader.Order;
import java.util.EnumSet;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import static org.testng.Assert.*;
import org.testng.annotations.Test;

/**
 * Test code for ReaderBuilderImpl.
 */
public class ReaderBuilderImplNGTest
{

  public ReaderBuilderImplNGTest()
  {
  }

  public ReaderBuilderImpl newInstance(Reader tr)
  {
    return new ReaderBuilderImpl(tr.getPackage(), tr.getDeclaration());
  }

  /**
   * Test of ReaderBuilderImpl constructor, of class ReaderBuilderImpl.
   */
  @Test
  public void testConstructor()
  {
    // Referenceable is true
    ReaderBuilderImpl instance = newInstance(TestReader.of(String.class));
    assertEquals(instance.uriName, "#" + TestPackage.namespace);
    assertNotNull(instance.parentGroup);
    assertNotNull(instance.handlerList);
    assertNull(instance.lastHandler);
    assertNull(instance.resultClass);
    assertNull(instance.elementName);
    assertNull(instance.producer);
    assertEquals(instance.parentGroup.flags, EnumSet.of(Option.OPTIONAL));

    // contentRequired is true
    instance = newInstance(TestReaderMixed.of(String.class));
    assertEquals(instance.uriName, "#" + TestPackage.namespace);
    assertNotNull(instance.parentGroup);
    assertNotNull(instance.handlerList);
    assertNull(instance.lastHandler);
    assertNull(instance.resultClass);
    assertNull(instance.elementName);
    assertNull(instance.producer);
    assertEquals(instance.parentGroup.flags, EnumSet.of(Option.REQUIRED));
  }

  /**
   * Test of ReaderBuilderImpl copy-constructor, of class ReaderBuilderImpl.
   */
  @Test
  public void testCopyConstructor()
  {
    ReaderBuilderImpl rbi = newInstance(TestReader.of(String.class));
    ReaderBuilderImpl instance = new ReaderBuilderImpl(rbi);
    assertSame(instance.uriName, rbi.uriName);
    assertSame(instance.resultClass, rbi.resultClass);
    assertSame(instance.elementName, rbi.elementName);
    assertSame(instance.handlerList, rbi.handlerList);
    assertSame(instance.parentGroup, rbi.parentGroup);
  }

  /**
   * Test of getHandlers method, of class ReaderBuilderImpl.
   *
   * @throws java.lang.Exception
   */
  @Test
  public void testGetHandlers() throws Exception
  {
    TestReader<String> tr = TestReader.of(String.class);
    ReaderBuilderImpl instance = newInstance(tr);
    Reader.ElementHandlerMap ehm = instance.getHandlers();
    assertNotNull(ehm);
    assertSame(ehm.getClass(), ElementHandlerMapImpl.class);

    // test ReaderException
    TestProducer tp = new TestProducer(tr);
    instance.producer = tp;
    try
    {
      instance.getHandlers();
    }
    catch (ReaderException re)
    {
      assertEquals(re.getMessage(), "Incomplete reader on getHandlers " + tp);
    }
  }

  /**
   * Test of element method, of class ReaderBuilderImpl.
   */
  @Test
  public void testElement()
  {
    ReaderBuilderImpl instance = newInstance(TestReader.of(String.class));
    String name = "name";

    ReaderBuilderImpl result = instance.element(name);
    assertSame(result, instance);
    assertNull(result.lastHandler);
    assertEquals(result.elementName, name + instance.uriName);
  }

  /**
   * Test of contents method, of class ReaderBuilderImpl.
   *
   * @throws java.lang.Exception
   */
  @Test
  public void testContents() throws Exception
  {
    // contents called reader method so more extensive test in reader method
    ReaderBuilderImpl instance = newInstance(TestReader.of(String.class));
    ReaderBuilderImpl result = instance.contents(Integer.class);
    assertSame(result, instance);
  }

  /**
   * Test of anyReader method, of class ReaderBuilderImpl.
   *
   * @throws java.lang.Exception
   */
  @Test
  public void testAny() throws Exception
  {
    // anyReader called reader method so more extensive test in reader method
    ReaderBuilderImpl instance = newInstance(TestReader.of(String.class));
    ReaderBuilderImpl result = instance.any(Integer.class);
    assertSame(result, instance);
  }

  /**
   * Test of reader method, of class ReaderBuilderImpl.
   *
   * @throws java.lang.Exception
   */
  @Test
  public void testReader() throws Exception
  {
    ReaderBuilderImpl instance = newInstance(TestReader.of(String.class));
    ReaderBuilderImpl result = instance.reader(ObjectReader.create(Integer.class));
    assertSame(result, instance);
    assertSame(result.resultClass, Integer.class);
    assertNull(result.lastHandler);
    assertNotNull(result.producer);
    assertEquals(result.producer.toString(), "reader for " + Integer.class.toString());

    // Test first producer null check
    TestReaderMixed trm = TestReaderMixed.of(Double.class);
    instance = newInstance(trm);
    instance.producer = new TestProducer(trm);
    AnyReader ar = AnyReader.of(Long.class);
    try
    {
      instance.reader(ar);
    }
    catch (ReaderException re)
    {
      assertEquals(re.getMessage(), "element contents redefined,\n    previous="
              + instance.producer.toString() + "\n    new=" + ar.toString());
    }

    // Test reader null check
    instance.producer = null;
    try
    {
      instance.reader(null);
    }
    catch (ReaderException re)
    {
      assertEquals(re.getMessage(), "reader is null");
    }

  }

  /**
   * Test of readers method, of class ReaderBuilderImpl.
   *
   * @throws java.lang.Exception
   */
  @Test
  public void testReaders() throws Exception
  {
    TestReader tr = TestReader.of(Double.class);
    TestReaderMixed trm = TestReaderMixed.of(Long.class);
    ReaderBuilderImpl instance = newInstance(tr);

    Reader.ReaderBuilderCall result = instance.readers(Integer.class, tr, trm);
    assertSame(result, instance);
    assertSame(((ReaderBuilderImpl) result).resultClass, Integer.class);
    // Can not access the array of ObjectReaders from the static private inner class
    // ReadersProducer. So we're just going to check if producer exists.
    assertNotNull(((ReaderBuilderImpl) result).producer);
    assertNull(((ReaderBuilderImpl) result).lastHandler);

    // Test producer not null check
    instance.producer = new TestProducer(trm);
    try
    {
      instance.readers(Integer.class, tr, trm);
    }
    catch (ReaderException re)
    {
      assertEquals(re.getMessage(), "element contents redefined");
    }

    // Test readers null check
    instance.producer = null;
    try
    {
      instance.readers(Integer.class, (ObjectReader) null);
    }
    catch (ReaderException re)
    {
      assertEquals(re.getMessage(), "reader is null");
    }

    // Test one reader
    instance.producer = null;
    instance.readers(Integer.class, tr);
  }

//  /**
//   * Test of using method, of class ReaderBuilderImpl.
//   */
//  @Test
//  public void testUsing()
//  {
//    TestReader tr = TestReader.of(Double.class);
//    ReaderBuilderImpl instance = newInstance(tr);
//    instance.target = "target";
//    instance.baseClass = String.class;
//
//    Integer target = Integer.parseInt("0");
//    Reader.ReaderBuilder result = instance.using(target);
//    assertNotSame(result, instance);
//
//    ReaderBuilderImpl resultImpl = (ReaderBuilderImpl) result;
//    assertSame(resultImpl.target, target);
//    assertSame(resultImpl.baseClass, target.getClass());
//    assertNotSame(resultImpl.target, instance.target);
//    assertNotSame(resultImpl.baseClass, instance.baseClass);
//    assertSame(resultImpl.uriName, instance.uriName);
//    assertSame(resultImpl.resultClass, instance.resultClass);
//    assertSame(resultImpl.elementName, instance.elementName);
//    assertSame(resultImpl.handlerList, instance.handlerList);
//    assertSame(resultImpl.parentGroup, instance.parentGroup);
//  }
  /**
   * Test of choice method, of class ReaderBuilderImpl.
   */
  @Test
  public void testChoice_ReaderOptionsArr()
  {
    TestReader tr = TestReader.of(Double.class);
    ReaderBuilderImpl instance = newInstance(tr);

    ReaderBuilderImpl result = instance.group(Order.CHOICE, (Option[]) null);
    assertNotSame(result, instance);
    assertNotNull(result.parentGroup);
    assertSame(result.parentGroup.getClass(), ElementGroupImpl.ChoiceGroup.class);
    assertNotSame(result.parentGroup, instance.parentGroup);
    assertSame(result.parentGroup.getParent(), instance.parentGroup);
    assertNull(result.parentGroup.flags);

    result = instance.group(Order.CHOICE, Option.ANY_ALL);
    assertTrue(result.parentGroup.flags.equals(EnumSet.of(Option.ANY_ALL)));

    result = instance.group(Order.CHOICE, Option.ANY_ALL, Option.ANY_LAX, Option.ANY_OTHER);
    assertTrue(result.parentGroup.flags.equals(EnumSet.of(Option.ANY_ALL, Option.ANY_LAX, Option.ANY_OTHER)));
  }

  /**
   * Test of choice method, of class ReaderBuilderImpl.
   */
  @Test
  public void testChoice_0args()
  {
    TestReader tr = TestReader.of(Double.class);
    ReaderBuilderImpl instance = newInstance(tr);

    ReaderBuilderImpl result = instance.group(Order.CHOICE);
    assertNotSame(result, instance);
    assertNotNull(result.parentGroup);
    assertSame(result.parentGroup.getClass(), ElementGroupImpl.ChoiceGroup.class);
    assertNotSame(result.parentGroup, instance.parentGroup);
    assertSame(result.parentGroup.getParent(), instance.parentGroup);
    assertNull(result.parentGroup.flags);
  }

  /**
   * Test of reference method, of class ReaderBuilderImpl.
   *
   * @throws java.lang.Exception
   */
  @Test
  public void testReference() throws Exception
  {
    TestReaderMixed trm = TestReaderMixed.of(Double.class);
    ReaderBuilderImpl instance = newInstance(trm);
    ReaderBuilderImpl result = instance.reference(Long.class);
    assertSame(result, instance);
    assertSame(result.resultClass, Long.class);
    assertNotNull(result.producer);
    assertEquals(result.producer.toString(), "reference");

    // Test ReaderException
    try
    {
      instance.reference(Long.class);
    }
    catch (ReaderException re)
    {
      assertEquals(re.getMessage(), "element contents redefined");
    }
  }

  /**
   * Test of section method, of class ReaderBuilderImpl.
   *
   * @throws java.lang.Exception
   */
  @Test
  public void testSection() throws Exception
  {
    TestSectionImpl section = new TestSectionImpl();

    TestReader tr = TestReader.of(Double.class);
    ReaderBuilderImpl instance = newInstance(tr);
    ReaderBuilderImpl result = (ReaderBuilderImpl) instance.section(section);
    assertSame(result, instance);
    assertNotNull(instance.lastHandler);
    assertTrue(instance.lastHandler.getOptions().equals(EnumSet.of(Option.NO_REFERENCE)));
    assertEquals(instance.lastHandler.getReader(), section);
  }

  /**
   * Test of setOptions method, of class ReaderBuilderImpl.
   */
  @Test
  public void testOptions()
  {
    ElementHandlerImpl handler1 = new ElementHandlerImpl("", null, null, null);
    ElementHandlerImpl handler2 = new ElementHandlerImpl("", null, null, null);
    handler2.setNextHandler(handler1);
    assertNull(handler1.options);
    assertNull(handler2.options);
    EnumSet<Option> flags = EnumSet.of(Option.UNBOUNDED, Option.REQUIRED);
    TestReaderMixed trm = TestReaderMixed.of(Double.class);
    ReaderBuilderImpl instance = newInstance(trm);
    instance.lastHandler = handler2;
    for (Option option : flags)
      instance.options(option);
    assertTrue(handler1.options.equals(flags));
    assertTrue(handler2.options.equals(flags));

    // Test RuntimeException
    try
    {
      instance.lastHandler = null;
      instance.options(Option.REQUIRED);
    }
    catch (RuntimeException re)
    {
      assertEquals(re.getMessage(), "null handler");
    }
  }

  /**
   * Test of getOptions method, of class ReaderBuilderImpl.
   */
  @Test
  public void testGetOptions()
  {
    ElementHandlerImpl handler1 = new ElementHandlerImpl("", null, null, null);
    EnumSet<Option> flags = EnumSet.of(Option.UNBOUNDED, Option.REQUIRED);
    TestReaderMixed trm = TestReaderMixed.of(Double.class);
    ReaderBuilderImpl instance = newInstance(trm);
    instance.lastHandler = handler1;
    assertNull(instance.lastHandler.getOptions());
    handler1.options = flags;
    assertTrue(instance.lastHandler.getOptions().equals(flags));
  }

  /**
   * Test of deferrable method, of class ReaderBuilderImpl.
   */
  @Test
  public void testDeferrable()
  {
    EnumSet<Option> flags = EnumSet.of(Option.DEFERRABLE);
    ElementHandlerImpl handler1 = new ElementHandlerImpl(null, null, null, null);
    TestReaderMixed trm = TestReaderMixed.of(Double.class);
    ReaderBuilderImpl instance = newInstance(trm);
    instance.lastHandler = handler1;
    ReaderBuilderImpl result = instance.deferrable();
    assertSame(result, instance);
    assertTrue(result.lastHandler.getOptions().equals(flags));
  }

  /**
   * Test of optional method, of class ReaderBuilderImpl.
   */
  @Test
  public void testOptional()
  {
    EnumSet<Option> flags = EnumSet.of(Option.OPTIONAL);
    ElementHandlerImpl handler1 = new ElementHandlerImpl(null, null, null, null);
    TestReaderMixed trm = TestReaderMixed.of(Double.class);
    ReaderBuilderImpl instance = newInstance(trm);
    instance.lastHandler = handler1;
    ReaderBuilderImpl result = instance.optional();
    assertSame(result, instance);
    assertTrue(result.lastHandler.getOptions().equals(flags));
  }

  /**
   * Test of required method, of class ReaderBuilderImpl.
   */
  @Test
  public void testRequired()
  {
    EnumSet<Option> flags = EnumSet.of(Option.REQUIRED);
    ElementHandlerImpl handler1 = new ElementHandlerImpl(null, null, null, null);
    TestReaderMixed trm = TestReaderMixed.of(Double.class);
    ReaderBuilderImpl instance = newInstance(trm);
    instance.lastHandler = handler1;
    ReaderBuilderImpl result = instance.required();
    assertSame(result, instance);
    assertTrue(result.lastHandler.getOptions().equals(flags));
  }

  /**
   * Test of unbounded method, of class ReaderBuilderImpl.
   */
  @Test
  public void testUnbounded()
  {
    EnumSet<Option> flags = EnumSet.of(Option.UNBOUNDED);
    ElementHandlerImpl handler1 = new ElementHandlerImpl(null, null, null, null);
    TestReaderMixed trm = TestReaderMixed.of(Double.class);
    ReaderBuilderImpl instance = newInstance(trm);
    instance.lastHandler = handler1;
    ReaderBuilderImpl result = instance.unbounded();
    assertSame(result, instance);
    assertTrue(result.lastHandler.getOptions().equals(flags));
  }

  /**
   * Test of define method, of class ReaderBuilderImpl.
   */
  @Test
  public void testDefine()
  {
    EnumSet<Option> flags = EnumSet.of(Option.NO_REFERENCE);
    ElementHandlerImpl handler1 = new ElementHandlerImpl(null, null, null, null);
    TestReaderMixed trm = TestReaderMixed.of(Double.class);
    ReaderBuilderImpl instance = newInstance(trm);
    instance.lastHandler = handler1;
    ReaderBuilderImpl result = instance.define();
    assertSame(result, instance);
    assertTrue(result.lastHandler.getOptions().equals(flags));
  }

  /**
   * Test of noid method, of class ReaderBuilderImpl.
   */
  @Test
  public void testNoid()
  {
    EnumSet<Option> flags = EnumSet.of(Option.NO_ID);
    ElementHandlerImpl handler1 = new ElementHandlerImpl(null, null, null, null);
    TestReaderMixed trm = TestReaderMixed.of(Double.class);
    ReaderBuilderImpl instance = newInstance(trm);
    instance.lastHandler = handler1;
    ReaderBuilderImpl result = instance.noid();
    assertSame(result, instance);
    assertTrue(result.lastHandler.getOptions().equals(flags));
  }

  /**
   * Test of executeCall method, of class ReaderBuilderImpl.
   *
   * @throws java.lang.Exception
   */
  @Test
  public void testExecuteCall() throws Exception
  {
    BiConsumer<String, String> method = (a, b) -> System.out.print("...testExecuteCall...");

    TestReader tr = TestReader.of(String.class);
    ReaderBuilderImpl instance = newInstance(tr);
    instance.reader(ObjectReader.create(Integer.class));
    instance.elementName = "elementName";
    ReaderBuilderImpl result = instance.executeCall(method);

    assertSame(result, instance);
    assertNull(result.producer);
    assertNull(result.elementName);
    assertNull(result.resultClass);
    assertNotNull(result.lastHandler);
    assertEquals(result.lastHandler.getClass(), ReaderHandler.class);
  }

  /**
   * Test of call method, of class ReaderBuilderImpl.
   */
  @Test
  public void testCall_BiConsumer() throws Exception
  {
    // mehtod call(BiConsumer<T, T2> method) calls executeCall so going to do 
    // simple testing
    TestReader tr = TestReader.of(String.class);
    ReaderBuilderImpl instance = newInstance(tr);
    instance.producer = new TestProducer(null);
    assertNull(instance.lastHandler);
    ReaderBuilderImpl result = instance.call((a, b) -> System.out.print("...testExecuteCall..."));
    assertNotNull(instance.lastHandler);
    assertSame(result, instance);
  }

  /**
   * Test of call method, of class ReaderBuilderImpl.
   *
   * @throws java.lang.Exception
   */
  @Test
  public void testCall_BiConsumer_Class() throws Exception
  {
    // mehtod call(BiConsumer<T, T3> method, Class<T3> resultClass)
    // evenutally calls executeCall so going to do simple testing

    BiConsumer<String, String> method = (a, b) -> System.out.print("testCall_BiConsumer_Class");

    // resultClass is null branch
    PrimitiveReaderImpl pri = new PrimitiveReaderImpl(INTEGER_PRIMITIVE);
    ReaderBuilderImpl instance = newInstance(pri);
    assertNull(instance.lastHandler);
    ReaderBuilderImpl result = instance.call(method, Integer.class);
    assertNotNull(instance.lastHandler);
    assertSame(result, instance);

    // ResultClass is not null branch, the else branch
    instance = newInstance(pri);
    instance.resultClass = Long.class;
    instance.producer = new TestProducer(null);
    assertNull(instance.lastHandler);
    result = instance.call(method, Long.class);
    assertNotNull(instance.lastHandler);
    assertSame(result, instance);

    // Test ReaderException
    instance.resultClass = String.class;
    try
    {
      instance.call(method, Long.class);
    }
    catch (ReaderException re)
    {
      assertEquals(re.getMessage(), "Can't assign " + instance.resultClass + " to " + Long.class);
    }
  }

  /**
   * Test of call method, of class ReaderBuilderImpl.
   *
   * @throws java.lang.Exception
   */
  @Test
  public void testCall_Consumer() throws Exception
  {
    // method call(Consumer<T> method) calls 
    // method  call(BiConsumer<T, T3> method, Class<T3> resultClass), which
    // evenutally calls executeCall so going to do simple testing
    Consumer<Integer> method = x -> System.out.println(x);

    PrimitiveReaderImpl pri = new PrimitiveReaderImpl(INTEGER_PRIMITIVE);
    ReaderBuilderImpl instance = newInstance(pri);

    assertNull(instance.lastHandler);
    ReaderBuilderImpl result = instance.call(method);
    assertNotNull(instance.lastHandler);
    assertSame(result, instance);
  }

  /**
   * Test of callString method, of class ReaderBuilderImpl.
   *
   * @throws java.lang.Exception
   */
  @Test
  public void testCallString() throws Exception
  {
    BiConsumer<String, String> method = (a, b) -> System.out.print(a + ":" + b);

    AnyReader ar = AnyReader.of(String.class);
    ReaderBuilderImpl instance = newInstance(ar);

    assertNull(instance.lastHandler);
    ReaderBuilderImpl result = instance.callString(method);
    assertNotNull(instance.lastHandler);
    assertSame(result, instance);
    assertSame(result.lastHandler.getObjectClass(), String.class);
//    assertSame(result.lastHandler.method, method);
  }

  /**
   * Test of callDouble method, of class ReaderBuilderImpl.
   *
   * @throws java.lang.Exception
   */
  @Test
  public void testCallDouble() throws Exception
  {
    BiConsumer<String, Double> method = (a, b) -> System.out.print(a + ":" + b);

    AnyReader ar = AnyReader.of(Double.class);
    ReaderBuilderImpl instance = newInstance(ar);

    assertNull(instance.lastHandler);
    ReaderBuilderImpl result = instance.callDouble(method);
    assertNotNull(instance.lastHandler);
    assertSame(result, instance);
    assertSame(result.lastHandler.getObjectClass(), Double.class);
//    assertSame(result.lastHandler.method, method);
  }

  /**
   * Test of callInteger method, of class ReaderBuilderImpl.
   *
   * @throws java.lang.Exception
   */
  @Test
  public void testCallInteger() throws Exception
  {
    BiConsumer<String, Integer> method = (a, b) -> System.out.print(a + ":" + b);

    AnyReader ar = AnyReader.of(Integer.class);
    ReaderBuilderImpl instance = newInstance(ar);

    assertNull(instance.lastHandler);
    ReaderBuilderImpl result = instance.callInteger(method);
    assertNotNull(instance.lastHandler);
    assertSame(result, instance);
    assertSame(result.lastHandler.getObjectClass(), Integer.class);
//    assertSame(result.lastHandler.method, method);
  }

  /**
   * Test of callLong method, of class ReaderBuilderImpl.
   *
   * @throws java.lang.Exception
   */
  @Test
  public void testCallLong() throws Exception
  {
    BiConsumer<String, Long> method = (a, b) -> System.out.print(a + ":" + b);

    AnyReader ar = AnyReader.of(Long.class);
    ReaderBuilderImpl instance = newInstance(ar);

    assertNull(instance.lastHandler);
    ReaderBuilderImpl result = instance.callLong(method);
    assertNotNull(instance.lastHandler);
    assertSame(result, instance);
    assertSame(result.lastHandler.getObjectClass(), Long.class);
//    assertSame(result.lastHandler.method, method);
  }

  /**
   * Test of callBoolean method, of class ReaderBuilderImpl.
   *
   * @throws java.lang.Exception
   */
  @Test
  public void testCallBoolean() throws Exception
  {
    BiConsumer<String, Boolean> method = (a, b) -> System.out.print(a + ":" + b);

    AnyReader ar = AnyReader.of(Boolean.class);
    ReaderBuilderImpl instance = newInstance(ar);

    assertNull(instance.lastHandler);
    ReaderBuilderImpl result = instance.callBoolean(method);
    assertNotNull(instance.lastHandler);
    assertSame(result, instance);
    assertSame(result.lastHandler.getObjectClass(), Boolean.class);
//    assertSame(result.lastHandler.method, method);
  }

  /**
   * Test of nop method, of class ReaderBuilderImpl.
   *
   * @throws java.lang.Exception
   */
  @Test
  public void testNop() throws Exception
  {
    // mehtod nop calls executeCall so going to do simple testing
    TestReader tr = TestReader.of(String.class);
    ReaderBuilderImpl instance = newInstance(tr);
    instance.producer = new TestProducer(null);
    assertNull(instance.lastHandler);
    instance.nop();
    assertNotNull(instance.lastHandler);
  }

  /**
   * Test of getKey method, of class ReaderBuilderImpl.
   */
  @Test
  public void testGetKey()
  {
    TestReader tr = TestReader.of(String.class);
    ReaderBuilderImpl instance = newInstance(tr);
    String value = "value";
    assertEquals(instance.getKey(value), value + "#" + TestPackage.namespace);
  }

  /**
   * Test of flag method, of class ReaderBuilderImpl.
   *
   * @throws java.lang.Exception
   */
  @Test
  public void testFlag() throws Exception
  {
    AnyReader ar = AnyReader.of(Boolean.class);
    ReaderBuilderImpl instance = newInstance(ar);
    ReaderBuilderImpl result = (ReaderBuilderImpl) instance.flag();
    assertSame(result, instance);
    assertSame(instance.resultClass, Boolean.class);
    assertEquals(instance.producer.toString(), "reader for " + Boolean.class);
  }

  /**
   * Test of list method, of class ReaderBuilderImpl.
   */
  @Test
  public void testList() throws Exception
  {
    TestReaderMixed trm = TestReaderMixed.of(String.class);
    ReaderBuilderImpl instance = newInstance(trm);
    ReaderBuilderImpl result = (ReaderBuilderImpl) instance.list(TestReader.of(String.class));
    assertSame(result, instance);
    assertSame(instance.resultClass, List.class);
    assertEquals(result.producer.toString(), "reader for " + List.class);
  }

  /**
   * Test of add method, of class ReaderBuilderImpl.
   */
  @Test
  public void testAdd()
  {
    TestReaderMixed trm = TestReaderMixed.of(String.class);
    ReaderBuilderImpl instance = newInstance(trm);

    assertNotNull(instance.handlerList);
    assertNull(instance.handlerList.firstHandler);
    assertNull(instance.handlerList.lastHandler);

    ElementHandlerImpl first = new ElementHandlerImpl(null, null, null, null);
    instance.add(first);
    assertEquals(instance.handlerList.firstHandler, first);
    assertEquals(instance.handlerList.lastHandler, first);

    ElementHandlerImpl second = new ElementHandlerImpl(null, null, null, null);
    instance.add(second);
    assertEquals(instance.handlerList.firstHandler, first);
    assertEquals(instance.handlerList.lastHandler, second);

    ElementHandlerImpl third = new ElementHandlerImpl(null, null, null, null);
    instance.add(third);
    assertEquals(instance.handlerList.firstHandler, first);
    assertEquals(instance.handlerList.lastHandler, third);

    ElementHandlerImpl iter = (ElementHandlerImpl) instance.handlerList.firstHandler;
    assertEquals(iter, first);

    iter = (ElementHandlerImpl) iter.getNextHandler();
    assertEquals(iter, second);

    iter = (ElementHandlerImpl) iter.getNextHandler();
    assertEquals(iter, third);

    iter = (ElementHandlerImpl) iter.getNextHandler();
    assertNull(iter);
  }

  class TestProducer<T, T2> implements Producer<T, T2>
  {
    public Reader<T2> reader;

    TestProducer(Reader<T2> reader)
    {
      this.reader = reader;
    }

    @Override
    public ElementHandlerImpl newInstance(ReaderBuilderImpl builder, String elementName, EnumSet<Option> flags, Class<T2> resultClass, BiConsumer<T, T2> method) throws ReaderException
    {
      return new ElementHandlerImpl<>(elementName, flags, resultClass, method);
    }
  }

  @Reader.Declaration(pkg = TestPackage.class,
          name = "#TestReaderAll", order = Reader.Order.ALL)
  static public class TestReaderAll<T> extends ObjectReader<T>
  {
    Class<T> cls;
    public T obj;

    public TestReaderAll()
    {
      this.cls = null;
    }

    public TestReaderAll(Class<T> cls)
    {
      this.cls = cls;
    }

    @Override
    public Class getObjectClass()
    {
      return cls;
    }
  }

  @Reader.Declaration(pkg = TestPackage.class,
          name = "#TestReaderAll", order = Reader.Order.CHOICE)
  static public class TestReaderChoice<T> extends ObjectReader<T>
  {
    Class<T> cls;
    public T obj;

    public TestReaderChoice()
    {
      this.cls = null;
    }

    public TestReaderChoice(Class<T> cls)
    {
      this.cls = cls;
    }

    @Override
    public Class getObjectClass()
    {
      return cls;
    }
  }

}
