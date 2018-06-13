﻿#pragma checksum "..\..\MainWindow.xaml" "{ff1816ec-aa5e-4d10-87f7-6f4963833460}" "F39E3C8B1E664BA5BCD08D0EBD2B670703ADCC4B"
//------------------------------------------------------------------------------
// <auto-generated>
//     This code was generated by a tool.
//     Runtime Version:4.0.30319.42000
//
//     Changes to this file may cause incorrect behavior and will be lost if
//     the code is regenerated.
// </auto-generated>
//------------------------------------------------------------------------------

using SilkScriptStyler;
using System;
using System.Diagnostics;
using System.Windows;
using System.Windows.Automation;
using System.Windows.Controls;
using System.Windows.Controls.Primitives;
using System.Windows.Data;
using System.Windows.Documents;
using System.Windows.Ink;
using System.Windows.Input;
using System.Windows.Markup;
using System.Windows.Media;
using System.Windows.Media.Animation;
using System.Windows.Media.Effects;
using System.Windows.Media.Imaging;
using System.Windows.Media.Media3D;
using System.Windows.Media.TextFormatting;
using System.Windows.Navigation;
using System.Windows.Shapes;
using System.Windows.Shell;


namespace SilkScriptStyler {
    
    
    /// <summary>
    /// MainWindow
    /// </summary>
    public partial class MainWindow : System.Windows.Window, System.Windows.Markup.IComponentConnector {
        
        
        #line 1 "..\..\MainWindow.xaml"
        [System.Diagnostics.CodeAnalysis.SuppressMessageAttribute("Microsoft.Performance", "CA1823:AvoidUnusedPrivateFields")]
        internal SilkScriptStyler.MainWindow Silk_Script_Styler;
        
        #line default
        #line hidden
        
        
        #line 20 "..\..\MainWindow.xaml"
        [System.Diagnostics.CodeAnalysis.SuppressMessageAttribute("Microsoft.Performance", "CA1823:AvoidUnusedPrivateFields")]
        internal System.Windows.Controls.TextBox inputText;
        
        #line default
        #line hidden
        
        
        #line 23 "..\..\MainWindow.xaml"
        [System.Diagnostics.CodeAnalysis.SuppressMessageAttribute("Microsoft.Performance", "CA1823:AvoidUnusedPrivateFields")]
        internal System.Windows.Controls.TextBox outputText;
        
        #line default
        #line hidden
        
        
        #line 25 "..\..\MainWindow.xaml"
        [System.Diagnostics.CodeAnalysis.SuppressMessageAttribute("Microsoft.Performance", "CA1823:AvoidUnusedPrivateFields")]
        internal System.Windows.Controls.CheckBox browserClickCheckbox;
        
        #line default
        #line hidden
        
        
        #line 26 "..\..\MainWindow.xaml"
        [System.Diagnostics.CodeAnalysis.SuppressMessageAttribute("Microsoft.Performance", "CA1823:AvoidUnusedPrivateFields")]
        internal System.Windows.Controls.CheckBox replaceThinkTimesCheckbox;
        
        #line default
        #line hidden
        
        
        #line 27 "..\..\MainWindow.xaml"
        [System.Diagnostics.CodeAnalysis.SuppressMessageAttribute("Microsoft.Performance", "CA1823:AvoidUnusedPrivateFields")]
        internal System.Windows.Controls.CheckBox removeBlankLinesCheckbox;
        
        #line default
        #line hidden
        
        
        #line 30 "..\..\MainWindow.xaml"
        [System.Diagnostics.CodeAnalysis.SuppressMessageAttribute("Microsoft.Performance", "CA1823:AvoidUnusedPrivateFields")]
        internal System.Windows.Controls.TextBox startMethodTextBox;
        
        #line default
        #line hidden
        
        
        #line 31 "..\..\MainWindow.xaml"
        [System.Diagnostics.CodeAnalysis.SuppressMessageAttribute("Microsoft.Performance", "CA1823:AvoidUnusedPrivateFields")]
        internal System.Windows.Controls.TextBox stopMethodTextBox;
        
        #line default
        #line hidden
        
        
        #line 33 "..\..\MainWindow.xaml"
        [System.Diagnostics.CodeAnalysis.SuppressMessageAttribute("Microsoft.Performance", "CA1823:AvoidUnusedPrivateFields")]
        internal System.Windows.Controls.TextBox variableTextBox;
        
        #line default
        #line hidden
        
        private bool _contentLoaded;
        
        /// <summary>
        /// InitializeComponent
        /// </summary>
        [System.Diagnostics.DebuggerNonUserCodeAttribute()]
        [System.CodeDom.Compiler.GeneratedCodeAttribute("PresentationBuildTasks", "4.0.0.0")]
        public void InitializeComponent() {
            if (_contentLoaded) {
                return;
            }
            _contentLoaded = true;
            System.Uri resourceLocater = new System.Uri("/Silk Script Styler;component/mainwindow.xaml", System.UriKind.Relative);
            
            #line 1 "..\..\MainWindow.xaml"
            System.Windows.Application.LoadComponent(this, resourceLocater);
            
            #line default
            #line hidden
        }
        
        [System.Diagnostics.DebuggerNonUserCodeAttribute()]
        [System.CodeDom.Compiler.GeneratedCodeAttribute("PresentationBuildTasks", "4.0.0.0")]
        [System.ComponentModel.EditorBrowsableAttribute(System.ComponentModel.EditorBrowsableState.Never)]
        [System.Diagnostics.CodeAnalysis.SuppressMessageAttribute("Microsoft.Design", "CA1033:InterfaceMethodsShouldBeCallableByChildTypes")]
        [System.Diagnostics.CodeAnalysis.SuppressMessageAttribute("Microsoft.Maintainability", "CA1502:AvoidExcessiveComplexity")]
        [System.Diagnostics.CodeAnalysis.SuppressMessageAttribute("Microsoft.Performance", "CA1800:DoNotCastUnnecessarily")]
        void System.Windows.Markup.IComponentConnector.Connect(int connectionId, object target) {
            switch (connectionId)
            {
            case 1:
            this.Silk_Script_Styler = ((SilkScriptStyler.MainWindow)(target));
            return;
            case 2:
            this.inputText = ((System.Windows.Controls.TextBox)(target));
            
            #line 20 "..\..\MainWindow.xaml"
            this.inputText.TextChanged += new System.Windows.Controls.TextChangedEventHandler(this.inputText_TextChanged);
            
            #line default
            #line hidden
            return;
            case 3:
            this.outputText = ((System.Windows.Controls.TextBox)(target));
            
            #line 23 "..\..\MainWindow.xaml"
            this.outputText.TextChanged += new System.Windows.Controls.TextChangedEventHandler(this.inputText_TextChanged);
            
            #line default
            #line hidden
            return;
            case 4:
            this.browserClickCheckbox = ((System.Windows.Controls.CheckBox)(target));
            
            #line 25 "..\..\MainWindow.xaml"
            this.browserClickCheckbox.Unchecked += new System.Windows.RoutedEventHandler(this.CheckBox_Changed);
            
            #line default
            #line hidden
            
            #line 25 "..\..\MainWindow.xaml"
            this.browserClickCheckbox.Checked += new System.Windows.RoutedEventHandler(this.CheckBox_Changed);
            
            #line default
            #line hidden
            return;
            case 5:
            this.replaceThinkTimesCheckbox = ((System.Windows.Controls.CheckBox)(target));
            
            #line 26 "..\..\MainWindow.xaml"
            this.replaceThinkTimesCheckbox.Unchecked += new System.Windows.RoutedEventHandler(this.CheckBox_Changed);
            
            #line default
            #line hidden
            
            #line 26 "..\..\MainWindow.xaml"
            this.replaceThinkTimesCheckbox.Checked += new System.Windows.RoutedEventHandler(this.CheckBox_Changed);
            
            #line default
            #line hidden
            return;
            case 6:
            this.removeBlankLinesCheckbox = ((System.Windows.Controls.CheckBox)(target));
            
            #line 27 "..\..\MainWindow.xaml"
            this.removeBlankLinesCheckbox.Unchecked += new System.Windows.RoutedEventHandler(this.CheckBox_Changed);
            
            #line default
            #line hidden
            
            #line 27 "..\..\MainWindow.xaml"
            this.removeBlankLinesCheckbox.Checked += new System.Windows.RoutedEventHandler(this.CheckBox_Changed);
            
            #line default
            #line hidden
            return;
            case 7:
            this.startMethodTextBox = ((System.Windows.Controls.TextBox)(target));
            
            #line 30 "..\..\MainWindow.xaml"
            this.startMethodTextBox.TextChanged += new System.Windows.Controls.TextChangedEventHandler(this.textBox_TextChanged);
            
            #line default
            #line hidden
            return;
            case 8:
            this.stopMethodTextBox = ((System.Windows.Controls.TextBox)(target));
            
            #line 31 "..\..\MainWindow.xaml"
            this.stopMethodTextBox.TextChanged += new System.Windows.Controls.TextChangedEventHandler(this.textBox_TextChanged);
            
            #line default
            #line hidden
            return;
            case 9:
            this.variableTextBox = ((System.Windows.Controls.TextBox)(target));
            
            #line 33 "..\..\MainWindow.xaml"
            this.variableTextBox.TextChanged += new System.Windows.Controls.TextChangedEventHandler(this.textBox_TextChanged);
            
            #line default
            #line hidden
            return;
            }
            this._contentLoaded = true;
        }
    }
}

