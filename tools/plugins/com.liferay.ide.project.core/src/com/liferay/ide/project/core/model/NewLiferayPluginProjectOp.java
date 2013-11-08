/*******************************************************************************
 * Copyright (c) 2000-2013 Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 *
 *******************************************************************************/
package com.liferay.ide.project.core.model;

import com.liferay.ide.core.ILiferayProjectProvider;
import com.liferay.ide.project.core.IPortletFramework;
import com.liferay.ide.project.core.model.internal.DisplayNameDefaultValueService;
import com.liferay.ide.project.core.model.internal.GroupIdValidationService;
import com.liferay.ide.project.core.model.internal.LocationListener;
import com.liferay.ide.project.core.model.internal.LocationValidationService;
import com.liferay.ide.project.core.model.internal.PluginTypeListener;
import com.liferay.ide.project.core.model.internal.PluginsSDKNameDefaultValueService;
import com.liferay.ide.project.core.model.internal.PluginsSDKNameListener;
import com.liferay.ide.project.core.model.internal.PluginsSDKNamePossibleValuesService;
import com.liferay.ide.project.core.model.internal.PluginsSDKNameValidationService;
import com.liferay.ide.project.core.model.internal.PortletFrameworkAdvancedPossibleValuesService;
import com.liferay.ide.project.core.model.internal.PortletFrameworkPossibleValuesService;
import com.liferay.ide.project.core.model.internal.PortletFrameworkValidationService;
import com.liferay.ide.project.core.model.internal.ProjectNameListener;
import com.liferay.ide.project.core.model.internal.ProjectNameValidationService;
import com.liferay.ide.project.core.model.internal.ProjectProviderDefaultValueService;
import com.liferay.ide.project.core.model.internal.ProjectProviderListener;
import com.liferay.ide.project.core.model.internal.ProjectProviderPossibleValuesService;
import com.liferay.ide.project.core.model.internal.RuntimeNameValidationService;
import com.liferay.ide.project.core.model.internal.UseDefaultLocationEnablementService;
import com.liferay.ide.project.core.model.internal.UseDefaultLocationListener;
import com.liferay.ide.project.core.model.internal.UseSdkLocationListener;

import org.eclipse.sapphire.ElementType;
import org.eclipse.sapphire.ExecutableElement;
import org.eclipse.sapphire.Value;
import org.eclipse.sapphire.ValueProperty;
import org.eclipse.sapphire.modeling.Path;
import org.eclipse.sapphire.modeling.ProgressMonitor;
import org.eclipse.sapphire.modeling.Status;
import org.eclipse.sapphire.modeling.annotations.AbsolutePath;
import org.eclipse.sapphire.modeling.annotations.DefaultValue;
import org.eclipse.sapphire.modeling.annotations.DelegateImplementation;
import org.eclipse.sapphire.modeling.annotations.Enablement;
import org.eclipse.sapphire.modeling.annotations.FileSystemResourceType;
import org.eclipse.sapphire.modeling.annotations.Label;
import org.eclipse.sapphire.modeling.annotations.Listeners;
import org.eclipse.sapphire.modeling.annotations.PossibleValues;
import org.eclipse.sapphire.modeling.annotations.Required;
import org.eclipse.sapphire.modeling.annotations.Service;
import org.eclipse.sapphire.modeling.annotations.Services;
import org.eclipse.sapphire.modeling.annotations.Type;
import org.eclipse.sapphire.modeling.annotations.ValidFileSystemResourceType;
import org.eclipse.sapphire.modeling.annotations.Whitespace;

/**
 * @author Gregory Amerson
 * @author Simon Jiang
 */
public interface NewLiferayPluginProjectOp extends ExecutableElement, HasLiferayRuntime
{

    ElementType TYPE = new ElementType( NewLiferayPluginProjectOp.class );

    // *** ProjectName ***

    @Label( standard = "project name" )
    @Listeners( ProjectNameListener.class )
    @Service( impl = ProjectNameValidationService.class )
    @Required
    ValueProperty PROP_PROJECT_NAME = new ValueProperty( TYPE, "ProjectName" ); //$NON-NLS-1$

    Value<String> getProjectName();

    void setProjectName( String value );

    // *** DisplayName ***

    @Label( standard = "display name" )
    @Service( impl = DisplayNameDefaultValueService.class )
    ValueProperty PROP_DISPLAY_NAME = new ValueProperty( TYPE, "DisplayName" ); //$NON-NLS-1$

    Value<String> getDisplayName();

    void setDisplayName( String value );

    // *** UseDefaultLocation ***

    @Type( base = Boolean.class )
    @DefaultValue( text = "true" )
    @Label( standard = "use default location" )
    @Listeners( UseDefaultLocationListener.class )
    @Service( impl = UseDefaultLocationEnablementService.class )
    ValueProperty PROP_USE_DEFAULT_LOCATION = new ValueProperty( TYPE, "UseDefaultLocation" ); //$NON-NLS-1$

    Value<Boolean> getUseDefaultLocation();

    void setUseDefaultLocation( String value );

    void setUseDefaultLocation( Boolean value );

    // *** ProjectLocation ***

    @Type( base = Path.class )
    @AbsolutePath
    @Enablement( expr = "${ UseDefaultLocation == 'false' }" )
    @ValidFileSystemResourceType( FileSystemResourceType.FOLDER )
    @Label( standard = "location" )
    @Service( impl = LocationValidationService.class )
    @Listeners( LocationListener.class )
    ValueProperty PROP_LOCATION = new ValueProperty( TYPE, "Location" ); //$NON-NLS-1$

    Value<Path> getLocation();

    void setLocation( String value );

    void setLocation( Path value );


    // *** ProjectProvider ***

    @Type( base = ILiferayProjectProvider.class )
    @Label( standard = "build type" )
    @Listeners( ProjectProviderListener.class )
    @Services
    (
        value=
        {
            @Service( impl = ProjectProviderPossibleValuesService.class ),
            @Service( impl = ProjectProviderDefaultValueService.class )
        }
    )
    ValueProperty PROP_PROJECT_PROVIDER = new ValueProperty( TYPE, "ProjectProvider" ); //$NON-NLS-1$

    Value<ILiferayProjectProvider> getProjectProvider();

    void setProjectProvider( String value );

    void setProjectProvider( ILiferayProjectProvider value );

    // *** UseSDKLocation ***

    @Type( base = Boolean.class )
    @DefaultValue( text = "true" )
    @Label( standard = "use sdk location" )
    @Listeners( UseSdkLocationListener.class )
    ValueProperty PROP_USE_SDK_LOCATION = new ValueProperty( TYPE, "UseSdkLocation" ); //$NON-NLS-1$

    Value<Boolean> getUseSdkLocation();

    void setUseSdkLocation( String value );

    void setUseSdkLocation( Boolean value );


    // *** PluginsSDKName ***

    @Label( standard = "Plugins SDK" )
    @Services
    (
        value =
        {
            @Service( impl = PluginsSDKNamePossibleValuesService.class ),
            @Service( impl = PluginsSDKNameDefaultValueService.class ),
            @Service( impl = PluginsSDKNameValidationService.class )
        }
    )
    @Listeners( PluginsSDKNameListener.class )
    ValueProperty PROP_PLUGINS_SDK_NAME = new ValueProperty( TYPE, "PluginsSDKName" ); //$NON-NLS-1$

    Value<String> getPluginsSDKName();

    void setPluginsSDKName( String value );

    // *** RuntimeName ***

    @Service( impl = RuntimeNameValidationService.class )
    ValueProperty PROP_RUNTIME_NAME = new ValueProperty( TYPE, HasLiferayRuntime.PROP_RUNTIME_NAME ); //$NON-NLS-1$


    // *** Version ***

    @Label( standard = "version" )
    @DefaultValue( text = "1.0.0-SNAPSHOT" )
    ValueProperty PROP_VERSION = new ValueProperty( TYPE, "Version" ); //$NON-NLS-1$

    Value<String> getVersion();

    void setVersion( String value );

    // *** PluginType ***

    @Type( base = PluginType.class )
    @Label( standard = "plugin type" )
    @DefaultValue( text = "Portlet" )
    @Listeners( PluginTypeListener.class )
    ValueProperty PROP_PLUGIN_TYPE = new ValueProperty( TYPE, "PluginType" ); //$NON-NLS-1$

    Value<PluginType> getPluginType();

    void setPluginType( String value );

    void setPluginType( PluginType value );

    // *** PortletFramework ***

    @Type( base = IPortletFramework.class )
    @Label( standard = "portlet framework" )
    @DefaultValue( text = "mvc" )
    @Services
    (
        value =
        {
            @Service( impl = PortletFrameworkPossibleValuesService.class ),
            @Service( impl = PortletFrameworkValidationService.class )
        }
    )
    ValueProperty PROP_PORTLET_FRAMEWORK = new ValueProperty( TYPE, "PortletFramework" ); //$NON-NLS-1$

    Value<IPortletFramework> getPortletFramework();

    void setPortletFramework( String value );

    void setPortletFramework( IPortletFramework value );

    // *** PortletFrameworkAdvanced ***

    @Type( base = IPortletFramework.class )
    @DefaultValue( text = "jsf" )
    @Service( impl = PortletFrameworkAdvancedPossibleValuesService.class )
    ValueProperty PROP_PORTLET_FRAMEWORK_ADVANCED = new ValueProperty( TYPE, "PortletFrameworkAdvanced" ); //$NON-NLS-1$

    Value<IPortletFramework> getPortletFrameworkAdvanced();

    void setPortletFrameworkAdvanced( String value );

    void setPortletFrameworkAdvanced( IPortletFramework value );

    // *** ThemeParent ***

    @Label( standard = "theme parent" )
    @PossibleValues( ordered = true, values = { "_unstyled", "_styled", "classic" } )
    @DefaultValue( text = "_styled" )
    ValueProperty PROP_THEME_PARENT = new ValueProperty( TYPE, "ThemeParent" ); //$NON-NLS-1$

    Value<String> getThemeParent();

    void setThemeParent( String value );

    // *** ThemeFramework ***

    @Label( standard = "theme framework" )
    @DefaultValue( text = "Freemarker" )
    @PossibleValues( ordered = true, values = { "Velocity", "Freemarker", "JSP" } )
    ValueProperty PROP_THEME_FRAMEWORK = new ValueProperty( TYPE, "ThemeFramework" ); //$NON-NLS-1$

    Value<String> getThemeFramework();

    void setThemeFramework( String value );

    // *** GroupId ***

    @Label( standard = "group id" )
    @DefaultValue( text = "com.example.plugins" )
    @Service( impl = GroupIdValidationService.class )
    @Whitespace( trim = false )
    ValueProperty PROP_GROUP_ID = new ValueProperty( TYPE, "GroupId" ); //$NON-NLS-1$

    Value<String> getGroupId();

    void setGroupId( String value );


    // *** ActiveProfiles ***

    @Label( standard = "profiles" )
    ValueProperty PROP_ACTIVE_PROFILES = new ValueProperty( TYPE, "ActiveProfiles" );

    Value<String> getActiveProfiles();

    void setActiveProfiles( String value );


    // *** FinalProjectName ***

    @DefaultValue( text = "${ProjectName}" )
    ValueProperty PROP_FINAL_PROJECT_NAME = new ValueProperty( TYPE, "FinalProjectName" );

    Value<String> getFinalProjectName();

    void setFinalProjectName( String value );

    // *** Method: execute ***

    @DelegateImplementation( NewLiferayPluginProjectOpMethods.class )
    Status execute( ProgressMonitor monitor );

}
