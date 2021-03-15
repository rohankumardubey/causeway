/*
 *  Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */
package org.apache.isis.commons.internal.debug.xray;

import java.util.Optional;
import java.util.Stack;
import java.util.UUID;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.MutableTreeNode;

public interface XrayModel {
    
    MutableTreeNode getRootNode();
    MutableTreeNode getThreadNode(String threadId);
    
    MutableTreeNode addContainerNode(MutableTreeNode parent, String name, String id);
    default MutableTreeNode addContainerNode(MutableTreeNode parent, String name) {
        return addContainerNode(parent, name, UUID.randomUUID().toString());
    }
    
    <T extends XrayDataModel> T addDataNode(MutableTreeNode parent, T dataModel);

    Optional<MutableTreeNode> lookupNode(String id);
    
    void remove(MutableTreeNode node);
    
    // -- DATA LOOKUP
    
    default Optional<XrayDataModel.Sequence> lookupSequence(String sequenceId) {
        return lookupNode(sequenceId)
        .map(node->
             (XrayDataModel.Sequence)((DefaultMutableTreeNode) node).getUserObject());
    }
    
    // -- STACKS
    
    Stack<MutableTreeNode> getNodeStack(String id);
    
    // -- ID AND LABEL
    
    abstract class HasIdAndLabel {
        public abstract String getId();
        public abstract String getLabel();
        
        @Override
        public final String toString() {
            return getLabel();
        }
        
    }

    

    
    
}
